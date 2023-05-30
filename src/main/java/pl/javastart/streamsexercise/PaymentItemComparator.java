package pl.javastart.streamsexercise;

import java.util.Comparator;

public class PaymentItemComparator implements Comparator<Payment> {


    @Override
    public int compare(Payment o1, Payment o2) {
        return Integer.compare(o1.getPaymentItems().size(), o2.getPaymentItems().size());
    }
}
