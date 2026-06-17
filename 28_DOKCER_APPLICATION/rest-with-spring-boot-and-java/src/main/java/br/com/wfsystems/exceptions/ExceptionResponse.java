package br.com.wfsystems.exceptions;

import java.util.Date;

public record ExceptionResponse(Date timestamp, String message, String details) {}
