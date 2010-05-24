package pcoaserver;

import java.net.*;
import java.io.*;

public class PcoaServer {
    public static void main(String[] args) throws IOException {

        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(4444);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 4444.");
            System.exit(1);
        }

        Socket clientSocket = null;
        try {
            clientSocket = serverSocket.accept();
        } catch (IOException e) {
            System.err.println("Accept failed.");
            System.exit(1);
        }

        DataOutputStream out = new DataOutputStream(clientSocket.getOutputStream());
        BufferedReader in = new BufferedReader(
				new InputStreamReader(
				clientSocket.getInputStream()));
        String inputLine, outputLine;

		//get the input data 
		inputLine = in.readLine();
		
		//get the distance metric
		String dist_metric = in.readLine();
		
		//write to file
		File outFile = new File("data.txt");
        FileWriter outw = new FileWriter(outFile);
		outw.write(inputLine);
        outw.close();
		
		//delete data files if they already exist
		File sample_coords = new File("sample_coords.txt");
		sample_coords.delete();
		File sp_coords = new File("sp_coords.txt");
		sp_coords.delete();
		
		try {
			System.out.println("Running python script...");
			//run python scripts to calculate PCoA
			System.out.println("Command: "+"python l19test.py " + "\"" + dist_metric + "\"");
			Process pr = Runtime.getRuntime().exec("python l19test.py " + dist_metric);
            BufferedReader br = new BufferedReader(new InputStreamReader(pr.getErrorStream()));
   			String line;
			while((line = br.readLine()) != null) {
				System.out.println(line);
			}
            int exitVal = pr.waitFor();
            System.out.println("Process Exit Value: " + exitVal);
			System.out.println("done.");
		} catch (Exception e) {
			System.out.println("Unable to run python script for PCoA analysis");
		}
		
		//send sp_coords.txt
		File myFile = new File ("sp_coords.txt");
        byte [] mybytearray  = new byte [(new Long(myFile.length())).intValue()];
        FileInputStream fis = new FileInputStream(myFile);
        System.out.println(".");
        
        //write file length
        System.out.println(myFile.length());
        out.writeInt((int)myFile.length());
        //write file
        for (int i = 0; i < myFile.length(); i++) {
        	out.writeByte(fis.read());
        }
        
        //send sample_coords.txt
		myFile = new File ("sample_coords.txt");
        mybytearray  = new byte [(int)myFile.length()];
        fis = new FileInputStream(myFile);
        fis.read(mybytearray);
        System.out.println(".");
        
        //write file length
        System.out.println(myFile.length());
        out.writeInt((int)myFile.length());
        //write file
        out.write(mybytearray); 
        
         //send evals.txt
		myFile = new File ("evals.txt");
        mybytearray  = new byte [(new Long(myFile.length())).intValue()];
        fis = new FileInputStream(myFile);
        fis.read(mybytearray);
        System.out.println(".");
        
        //write file length
        System.out.println(myFile.length());
        out.writeInt((int)myFile.length());
        //write file
        out.write(mybytearray);      
        
		out.flush();
        out.close();
        in.close();
        clientSocket.close();
        serverSocket.close();
    }
}