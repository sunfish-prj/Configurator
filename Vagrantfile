Vagrant.configure("2") do |config|

  config.vm.define "saltmaster", primary: true do |saltmaster|
    saltmaster.vm.provider "virtualbox" do |v|
      v.memory = 1024
      v.cpus = 1
    end
    saltmaster.vm.box = "bento/ubuntu-16.04"
    saltmaster.vm.hostname = "saltmaster"
    saltmaster.vm.network "private_network", ip: "192.168.71.100"
    saltmaster.vm.synced_folder "saltstack/salt/", "/srv/salt", type: "rsync"
    saltmaster.vm.synced_folder "saltstack/pillar/", "/srv/pillar", type: "rsync"
    saltmaster.vm.synced_folder "clients", "/root/clients", type: "rsync"
    saltmaster.vm.synced_folder "configurator", "/root/configurator", type: "rsync"
    saltmaster.vm.provision :salt do |salt|
      salt.install_master = true
      salt.minion_id = "saltmaster"
      salt.master_config = "saltstack/conf/master"
      salt.minion_config = "saltstack/conf/minion"
      salt.master_key = "saltstack/keys/saltmaster.pem"
      salt.master_pub = "saltstack/keys/saltmaster.pub"
      salt.minion_key = "saltstack/keys/saltmaster.pem"
      salt.minion_pub = "saltstack/keys/saltmaster.pub"
      salt.seed_master = {
                    "saltmaster" => "saltstack/keys/saltmaster.pub",
                    "master" => "saltstack/keys/master.pub",
                    "1" => "saltstack/keys/node1.pub",
                    "2" => "saltstack/keys/node2.pub"
                   }
      salt.colorize = true
    end
  end

  config.vm.define "master" do |master|
    master.vm.provider "virtualbox" do |v|
      v.memory = 1024
      v.cpus = 2
    end
    master.vm.box = "bento/ubuntu-16.04"
    master.vm.hostname = "master"
    master.vm.network "private_network", ip: "192.168.71.101"
    master.vm.provision :salt do |salt|
      salt.minion_id = "master"
      salt.minion_config = "saltstack/conf/minion"
      salt.minion_key = "saltstack/keys/master.pem"
      salt.minion_pub = "saltstack/keys/master.pub"
      salt.colorize = true
    end
  end

  config.vm.define "node1" do |node1|
    node1.vm.provider "virtualbox" do |v|
      v.memory = 1024
      v.cpus = 1
    end
    node1.vm.box = "bento/ubuntu-16.04"
    node1.vm.hostname = "1"
    node1.vm.network "private_network", ip: "192.168.71.102"
    node1.vm.provision :salt do |salt|
      salt.minion_id = "1"
      salt.minion_config = "saltstack/conf/minion"
      salt.minion_key = "saltstack/keys/node1.pem"
      salt.minion_pub = "saltstack/keys/node1.pub"
      salt.colorize = true
    end
  end

  config.vm.define "node2" do |node2|
    node2.vm.provider "virtualbox" do |v|
      v.memory = 1024
      v.cpus = 1
    end
    node2.vm.box = "bento/ubuntu-16.04"
    node2.vm.hostname = "2"
    node2.vm.network "private_network", ip: "192.168.71.103"
    node2.vm.provision :salt do |salt|
      salt.minion_id = "2"
      salt.minion_config = "saltstack/conf/minion"
      salt.minion_key = "saltstack/keys/node2.pem"
      salt.minion_pub = "saltstack/keys/node2.pub"
      salt.colorize = true
    end
  end

end
