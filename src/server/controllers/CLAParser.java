package server.controllers;

import common.models.Grid;

import java.awt.Dimension;

import server.content.CreateGridException;
import server.content.GridGenerator;
import server.content.GridLoader;

public class CLAParser {
    public static Grid parse(String[] args) {
        Grid grid = null;
        
        boolean showHelp = false;
        if (args.length > 0) {
            switch (args[0]) {
                case "load":
                    if (args.length == 2) {
                        try {
                            grid = GridLoader.loadGrid(args[1]);
                        } catch(CreateGridException e) {
                            System.out.println(e.getMessage());
                            showHelp = true;
                        }
                    } else {
                        showHelp = true;
                    }   break;
                case "random":
                    if (args.length == 3 || args.length == 4) {
                        try {
                            Dimension dimension = new Dimension(Integer.parseInt(args[1]), Integer.parseInt(args[2]));
                            if (args.length == 3) {
                                grid = GridGenerator.createRandomGrid(dimension);
                            } else {
                                grid = GridGenerator.createRandomGrid(dimension, Integer.parseInt(args[3]));
                            }
                        } catch(NumberFormatException e) {
                            showHelp = true;
                        }
                    } else {
                        showHelp = true;
                    }   break;
                case "help":
                    showHelp = true;
                    break;
            }
        } else {
            showHelp = true;
        }
        
        if (showHelp) {
            System.out.println("Usage: server <command>");
            System.out.println("Available commands:");
            System.out.println("    load <gridName>                   Load a predefined json grid");
            System.out.println("    random <width> <height> [<seed>]  Generate a random grid");
            System.out.println("    help                              Show this help file");
            System.exit(0);
        }
        
        return grid;
    }
}
