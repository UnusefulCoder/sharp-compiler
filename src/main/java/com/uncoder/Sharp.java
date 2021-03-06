package com.uncoder;

import java.io.IOException;

import com.uncoder.tree.Tree;

public class Sharp {
    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.out.println("Pass the name of the file to compile.\n");
            return;
        }

        Parser parser = new Parser(args[0]);

        for (Tree tree : parser.parse()) {
            System.out.println(tree);
        }

        System.out.println();
    }
}
