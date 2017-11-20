package saltop

// TODO: ERROR MANAGEMENT

import (
	"bytes"
	"crypto/tls"
	"encoding/json"
	"errors"
	"fmt"
	"io/ioutil"
	"net/http"
	"net/http/cookiejar"
	"strings"
	"time"
)

//Url api Saltstack
const urlapi = "https://localhost:8000"
const username = `"saltuser"`
const password = `"saltuser"`

//SaltClient manage a Saltstack client session
type SaltClient struct {
	hclient *http.Client
}

type localCmd struct {
	Client string            `json:"client"`
	Tgt    string            `json:"tgt"`
	Fun    string            `json:"fun"`
	Arg    []string          `json:"arg"`
	Kwarg  map[string]string `json:"kwarg"`
}

type cmdResult struct {
	MinionsResults map[string]minionResult
}

type minionResult struct {
	StatesResults map[string]map[string]json.RawMessage
}

//Login to the api
func Login() (SaltClient, error) {
	var client SaltClient
	client.hclient = &http.Client{}
	client.hclient.Timeout = 0
	tr := &http.Transport{
		TLSClientConfig: &tls.Config{InsecureSkipVerify: true},
	}
	client.hclient.Transport = tr
	cookieJar, err := cookiejar.New(nil)
	if err != nil {
		panic(err)
	}
	client.hclient.Jar = cookieJar
	var jsonStr = []byte(`{
		"username":` + username + `,
		"password":` + password + `,
		"eauth":"pam"}`)
	url := urlapi + "/login"
	req, err := http.NewRequest(http.MethodPost, url, bytes.NewBuffer(jsonStr))
	if err != nil {
		panic(err)
	}
	setHeader(req.Header)
	resp, err := client.hclient.Do(req)
	if err != nil {
		panic(err)
	}
	defer resp.Body.Close()
	if resp.StatusCode != 200 {
		err := errors.New(resp.Status)
		panic(err)
	}
	return client, nil
}

//Logout invalidate SaltClient current session
func (client *SaltClient) Logout() error {
	var jsonStr []byte
	url := urlapi + "/logout"
	req, err := http.NewRequest(http.MethodPost, url, bytes.NewBuffer(jsonStr))
	if err != nil {
		panic(err)
	}
	setHeader(req.Header)
	resp, err := client.hclient.Do(req)
	if err != nil {
		panic(err)
	}
	if resp.StatusCode != 200 {
		err := errors.New(resp.Status)
		panic(err)
	}
	return nil
}

//SetCa set a target as a cluster certificate authority
func (client *SaltClient) SetCa(target string) error {
	_, err := client.applyState(target, "k8s.ca")
	// Applying k8s.ca make the minion restart. making the salt api returning a EOF and restarting itself.
	// Workaround: wait for api restart and do a new a login
	if err != nil {
		if strings.HasSuffix(err.Error(), "EOF") == true {
			var d time.Duration = 5 * time.Second
			time.Sleep(d)
			*client, _ = Login()
			return nil
		}
		return err
	}
	return nil
}

//SetMaster set a target as master of the kubernetes cluster
func (client *SaltClient) SetMaster(target string) error {
	_, err := client.applyState(target, "docker")
	if err != nil {
		panic(err)
	}
	_, err = client.applyState(target, "k8s")
	if err != nil {
		panic(err)
	}
	_, err = client.applyState(target, "k8s.master")
	if err != nil {
		panic(err)
	}
	return nil
}

//SetNode set target as a simple node of the kubernetes cluster of master master
func (client *SaltClient) SetNode(target string, master string) error {
	_, err := client.applyState(target, "docker")
	if err != nil {
		panic(err)
	}
	_, err = client.applyState(target, "k8s")
	if err != nil {
		panic(err)
	}
	cmdRes, err := client.applyState(target, "k8s.node")
	if err != nil {
		panic(err)
	}
	for minion := range cmdRes.MinionsResults {
		arg := map[string]string{"name": minion}
		_, err = client.applySingleState(master, "k8s_custom.node_is_present", arg)
		if err != nil {
			panic(err)
		}
	}
	return nil
}

//ApplyLabels apply all the labels (previously set on the master pillar) in the kubernetes cluster with master target
func (client *SaltClient) ApplyLabels(target string) error {
	_, err := client.applyState(target, "k8s.master.node_label")
	return err
}

//DeployYaml deploy all yamls (previously set on the master pillar) in the kubernetes cluster with master target
func (client *SaltClient) DeployYaml(target string) error {
	_, err := client.applyState(target, "k8s.master.deploy_yaml")
	return err
}

//ResetRoleNode reset a node (detach it) from a kubernetes cluster
func (client *SaltClient) ResetRoleNode(node string, master string) error {
	arg := map[string]string{
		"name": node,
	}
	_, err := client.applySingleState(master, "k8s_custom.node_drained", arg)
	if err != nil {
		panic(err)
	}
	_, err = client.applySingleState(master, "k8s_custom.node_absent", arg)
	if err != nil {
		panic(err)
	}
	_, err = client.applyState(node, "k8s.reset_role")
	if err != nil {
		panic(err)
	}
	return nil
}

func newcmdResult() cmdResult {
	var cmdRes cmdResult
	cmdRes.MinionsResults = make(map[string]minionResult)
	return cmdRes
}

func newminionResult() minionResult {
	var minionRes minionResult
	minionRes.StatesResults = make(map[string]map[string]json.RawMessage)
	return minionRes
}

func (client *SaltClient) applyState(tgt string, state string) (cmdResult, error) {
	var cmd localCmd
	cmd.Client = "local"
	cmd.Tgt = tgt
	cmd.Fun = "state.apply"
	cmd.Arg = append(cmd.Arg, state)
	cmdRes, err := client.sendCmd(cmd)
	if err != nil {
		return cmdRes, err
	}
	_, err = cmdRes.checkFailure()
	if err != nil {
		return cmdRes, errors.New("Failed applying state")
	}
	return cmdRes, nil
}

func (client *SaltClient) applySingleState(tgt string, state string, arg map[string]string) (cmdResult, error) {
	var cmd localCmd
	cmd.Client = "local"
	cmd.Tgt = tgt
	cmd.Fun = "state.single"
	cmd.Arg = append(cmd.Arg, state)
	cmd.Kwarg = arg
	cmdRes, err := client.sendCmd(cmd)
	if err != nil {
		panic(err)
	}
	_, err = cmdRes.checkFailure()
	if err != nil {
		return cmdRes, errors.New("Failed applying state")
	}
	return cmdRes, nil
}

func (client *SaltClient) sendCmd(cmd localCmd) (cmdResult, error) {
	var cmdRes cmdResult
	bodyJSON, err := json.Marshal(cmd)
	if err != nil {
		panic(err)
	}
	req, err := http.NewRequest(http.MethodPost, urlapi, bytes.NewBuffer(bodyJSON))
	if err != nil {
		panic(err)
	}
	setHeader(req.Header)
	resp, err := client.hclient.Do(req)
	if err != nil {
		return cmdRes, err
	}
	if resp.StatusCode != 200 {
		err = errors.New(resp.Status)
		return cmdRes, err
	}
	cmdRes, err = parseResults(resp)
	if err != nil {
		return cmdRes, err
	}
	return cmdRes, nil
}

func parseResults(resp *http.Response) (cmdResult, error) {
	body, _ := ioutil.ReadAll(resp.Body)
	var res map[string][]map[string]map[string]map[string]json.RawMessage
	err := json.Unmarshal(body, &res)
	if err != nil {
		panic(err)
	}
	cmdRes := newcmdResult()
	var minRes minionResult
	for key, value := range res["return"][0] {
		minRes.StatesResults = value
		cmdRes.MinionsResults[key] = minRes
	}
	return cmdRes, nil
}

func (cmdRes cmdResult) checkFailure() (cmdResult, error) {
	totalStatesFailed := newcmdResult()
	for minion, minRes := range cmdRes.MinionsResults {
		minFailedStates := newminionResult()
		for state, stateRes := range minRes.StatesResults {
			var stateResultBool bool
			err := json.Unmarshal(stateRes["result"], &stateResultBool)
			if err != nil {
				panic(err)
			}
			if stateResultBool == false {
				minFailedStates.StatesResults[state] = stateRes
			}
		}
		if len(minFailedStates.StatesResults) > 0 {
			totalStatesFailed.MinionsResults[minion] = minFailedStates
		}
	}
	return totalStatesFailed, nil
}

func setHeader(head http.Header) {
	head.Set("Accept", "application/json")
	head.Set("Content-type", "application/json")
}

func printResp(resp *http.Response) {
	fmt.Println("response Status:", resp.Status)
	fmt.Println("response Headers:", resp.Header)
	body, _ := ioutil.ReadAll(resp.Body)
	var m map[string]*json.RawMessage
	json.Unmarshal(body, &m)
	b, _ := json.MarshalIndent(m, "", "  ")
	fmt.Println("response Body:", string(b))
}
