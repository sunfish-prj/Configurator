package configurator.salt;

import com.google.gson.reflect.TypeToken;
import com.suse.salt.netapi.AuthModule;
import com.suse.salt.netapi.calls.LocalCall;
import com.suse.salt.netapi.calls.modules.State;
import com.suse.salt.netapi.client.SaltClient;
import com.suse.salt.netapi.config.ClientConfig;
import com.suse.salt.netapi.datatypes.target.Glob;
import com.suse.salt.netapi.datatypes.target.Target;
import com.suse.salt.netapi.exception.SaltException;
import com.suse.salt.netapi.results.Result;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.*;


@Service
public class SaltService {

    private SaltClient saltClient;

    final static private String SALTUSER = "saltuser";
    final static private String SALTPSW = "saltuser";
    final static private String SALTURL = "https://localhost:8000";
    private Target <String> ca;
    private Target <String> master;

    public SaltService() throws SaltException {
        saltClient = new SaltClient(URI.create(SALTURL));
        ClientConfig clientConfig = saltClient.getConfig();
        clientConfig.put(ClientConfig.SOCKET_TIMEOUT, 0);
        try {
            saltClient.login(SALTUSER, SALTPSW, AuthModule.PAM);
        }
        catch (SaltException exc) {
            throw exc;
        }
    }

    public void startupCluster() throws SaltException {
        this.ca = new Glob("saltmaster");
        this.master = new Glob("master");
        Map<String, Result<Map<String, State.ApplyResult>>> result;
        result = State.apply("k8s.ca").callSync(saltClient, ca);
        checkStateResult(result);
        result = State.apply("docker").callSync(saltClient, master);
        checkStateResult(result);
        result = State.apply("k8s").callSync(saltClient, master);
        checkStateResult(result);
        result = State.apply("k8s.master").callSync(saltClient, master);
        checkStateResult(result);
    }

    public void containerizedVM(Integer vmID) throws  SaltException {
        Target <String> node;
        node = new Glob(vmID.toString());
        Map<String, Result<Map<String, State.ApplyResult>>> result;
        result = State.apply("docker").callSync(saltClient, node);
        checkStateResult(result);
        result = State.apply("k8s").callSync(saltClient, node);
        checkStateResult(result);
    }

    public void addNode(Integer vmID) throws  SaltException {
        Target <String> node;
        node = new Glob(vmID.toString());
        Map<String, Result<Map<String, State.ApplyResult>>> result;
        result = State.apply("k8s.node").callSync(saltClient, node);
        checkStateResult(result);
        List<String> args = new LinkedList<>();
        Map<String, String> kwargs = new HashMap<>();
        args.add("k8s_custom.node_is_present");
        kwargs.put("name", vmID.toString());
        LocalCall<Map<String, State.ApplyResult>>singleStateCall = new LocalCall<>("state.single",
                Optional.of(args),
                Optional.of(kwargs),
                new TypeToken<Map<String, State.ApplyResult>>(){});
        result = singleStateCall.callSync(saltClient, master);
        checkStateResult(result);
    }

    public void removeNode(Integer nodeID) throws  SaltException {
        Target <String> node;
        node = new Glob(nodeID.toString());
        Map<String, Result<Map<String, State.ApplyResult>>> result;
        List<String> args = new LinkedList<>();
        Map<String, String> kwargs = new HashMap<>();
        args.add("k8s_custom.node_drained");
        kwargs.put("name", nodeID.toString());
        LocalCall<Map<String, State.ApplyResult>> singleStateCall = new LocalCall<>("state.single",
                Optional.of(args),
                Optional.of(kwargs),
                new TypeToken<Map<String, State.ApplyResult>>(){});
        result = singleStateCall.callSync(saltClient, master);
        checkStateResult(result);
        args = new LinkedList<>();
        kwargs = new HashMap<>();
        args.add("k8s_custom.node_absent");
        kwargs.put("name", nodeID.toString());
        singleStateCall = new LocalCall<>("state.single",
                Optional.of(args),
                Optional.of(kwargs),
                new TypeToken<Map<String, State.ApplyResult>>(){});
        result = singleStateCall.callSync(saltClient, master);
        checkStateResult(result);
        result = State.apply("k8s.reset_role").callSync(saltClient, node);
        checkStateResult(result);
    }

    private void checkStateResult (Map<String, Result<Map<String, State.ApplyResult>>> result) throws SaltException {
        boolean failed = false;
        for (Map.Entry<String, Result<Map<String, State.ApplyResult>>> res:
             result.entrySet()) {
            failed = res.getValue().fold(
                    stateError -> {
                        return true;
                    },
                    mapResult -> {
                        for (Map.Entry<String, State.ApplyResult> stateResult:
                             mapResult.entrySet()) {
                            if (stateResult.getValue().isResult() == false){
                                return true;
                            }
                        }
                        return false;
                    }
            );
        }
        if (failed == true) {
            throw new SaltException("States execution failed");
        }
    }

}
