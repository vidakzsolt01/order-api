package hu.gov.allamkincstar.training.javasebsc.orderapi.exceptions;

import hu.gov.allamkincstar.training.javasebsc.orderapi.enums.PaymentModeEnum;

public class InvalidPaymentModeException extends Exception{
    private static final String DEFAULT_MESSAGE = "Ez a fizetési mód ennél a megrendeléstípusnál nem választható";

    public InvalidPaymentModeException(PaymentModeEnum paymentMode) {
        super(DEFAULT_MESSAGE+": "+paymentMode);
    }
}
