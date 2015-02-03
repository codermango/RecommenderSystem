package com.utility;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class MovieDataConvert {
	
	public static void generateFormatedMovieFile(String inputFilePath, String outputFilePath) {
		BufferedReader br;
		BufferedWriter bw;
		try {
			br = new BufferedReader(new FileReader(inputFilePath));
			bw = new BufferedWriter(new FileWriter(outputFilePath));
			
			String line;
			while((line = br.readLine()) != null) {
				String[] values = line.split("::", -1);
				bw.write(values[0] + "," + values[1] + "," + values[2] + "\r\n");
			}
			
			br.close();
			bw.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
	}
	
	public static void generateFormatedUserFile(String inputFilePath, String outputFilePath) {
		BufferedReader br;
		BufferedWriter bw;
		try {
			br = new BufferedReader(new FileReader(inputFilePath));
			bw = new BufferedWriter(new FileWriter(outputFilePath));
			
			String line;
			while((line = br.readLine()) != null) {
				String[] values = line.split("::", -1);
				bw.write(values[0] + "," + values[1] + "," + values[2] + "," + values[3] + "," + values[4] + "\r\n");
			}
			
			br.close();
			bw.close();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
	}
	
	
	public static void main(String args[]) {
		generateFormatedMovieFile("data/movies.dat", "data/movies.csv");
		generateFormatedUserFile("data/users.dat", "data/users.csv");
	}
	
	
}































