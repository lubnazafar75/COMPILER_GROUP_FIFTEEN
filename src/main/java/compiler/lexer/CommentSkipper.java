package compiler.lexer;

import compiler.error.ErrorReporter;

public final class CommentSkipper {
    public boolean isCommentStart(SourceReader reader) {
        return reader.peek() == '/'
            && (reader.peekNext() == '/' || reader.peekNext() == '*');
    }
    public void skip(SourceReader reader, ErrorReporter errors) {
        int line = reader.getLine();
        int column = reader.getColumn();
        reader.advance();
        char kind = reader.advance();
        if (kind == '/') {
            while (!reader.isAtEnd() && reader.peek() != '\n') {
                reader.advance();
            }
            return;
        }
        while (!reader.isAtEnd()) {
            if (reader.peek() == '*' && reader.peekNext() == '/') {
                reader.advance();
                reader.advance();
                    return;
            }
            reader.advance();
        }
        errors.unterminatedComment(line, column);
    }
}
