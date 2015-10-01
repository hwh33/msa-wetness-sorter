package main

import (
	"fmt"
	"net/http"
	"os"
)

func main() {
	// Check that the expected arguments are provided.
	if len(os.Args) < 3 {
		fmt.Println("Server address and file server root directory must be supplied")
		os.Exit(1)
	}

	serverAddress := os.Args[1]
	fileServerRoot := os.Args[2]

	// Check that the file server root exists and is a directory.
	rootInfo, err := os.Stat(fileServerRoot)
	if os.IsNotExist(err) {
		fmt.Println("File server root directory not found")
		os.Exit(1)
	}
	if !rootInfo.IsDir() {
		fmt.Println("File server root is not a directory")
		os.Exit(1)
	}

	// Start up the file server.
	fileServer := http.FileServer(http.Dir(fileServerRoot))
	http.ListenAndServe(serverAddress, fileServer)

	// If this point is reached then the provided address was invalid.
	fmt.Println("Invalid address provided for server")
	os.Exit(1)
}
