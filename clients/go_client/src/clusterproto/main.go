package main

import (
	saltop "clusterproto/saltop"
	"fmt"
)

//Test/example of saltop
func main() {
	client, _ := saltop.Login()
	fmt.Println("Login succesful")

	client.SetCa("saltmaster")
	fmt.Println("CA up")

	client.SetMaster("master")
	fmt.Println("Master up")

	client.SetNode("node*", "master")
	fmt.Println("Nodes up")

	client.ApplyLabels("master")
	fmt.Println("Labels applied")

	client.DeployYaml("master")
	fmt.Println("Yaml deployed")

	client.Logout()
	fmt.Println("Logged out")
}
