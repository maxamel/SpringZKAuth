package com.github.maxamel.client.java;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.Scanner;
import java.util.stream.Collectors;

public class ZKClient{

    private String[] commands = {"PUT","GET","DELETE"};
    
    public static void main(String [] args)
    {
        Scanner scanner = new Scanner( System.in );
        while (true)
        {      
            System.out.print(">");
            String line = scanner.nextLine();
            String[] input = line.trim().replaceAll(" +", " ").split(" ");
            LinkedList<String> list = new LinkedList<>(Arrays.asList(input));
            
            String command = list.get(0);
            String url = list.get(1);
            String json = list.subList(2, list.size()).stream().collect(Collectors.joining());
            
            System.out.println(command + url + json);
        }
    }

}
