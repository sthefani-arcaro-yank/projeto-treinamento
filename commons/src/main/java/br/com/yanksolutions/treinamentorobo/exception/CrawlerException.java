package br.com.yanksolutions.treinamentorobo.exception;

public class CrawlerException extends RuntimeException {
    public CrawlerException() {
    }

    public CrawlerException(String message) {
        super(message);
    }

    public CrawlerException(String message, Throwable cause) {
        super(message, cause);
    }
}
