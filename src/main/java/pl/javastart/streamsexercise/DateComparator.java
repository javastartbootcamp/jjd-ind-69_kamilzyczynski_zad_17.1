package pl.javastart.streamsexercise;

import java.util.Comparator;

public class DateComparator implements Comparator<Payment> {

    @Override
    public int compare(Payment o1, Payment o2) {
        return o1.getPaymentDate().compareTo(o2.getPaymentDate());
    }
}
