package compiler;

import compiler.lexer.Lexer;

public class Main {
    public static void main(String[] args) {
        System.out.println("COMPILER_GROUP_FIFTEEN is ready.");

        // Driver test: runs whitespace/EOF verification
        String testInput = "   \n\t  ";
        Lexer lexer = new Lexer(testInput);

        Token token;
        do {
            token = lexer.nextToken();
            System.out.println(token);
        } while (token.getType() != TokenType.EOF);
    }
}