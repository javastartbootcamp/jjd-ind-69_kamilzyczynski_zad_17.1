package pl.javastart.streamsexercise;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

public class Payment {

    private User user;
    private ZonedDateTime paymentDate;
    private List<PaymentItem> paymentItems;

    public Payment(User user, ZonedDateTime paymentDate, List<PaymentItem> paymentItems) {
        this.user = user;
        this.paymentDate = paymentDate;
        this.paymentItems = paymentItems;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ZonedDateTime getPaymentDate() {
        return paymentDate;
    }

    public void setPaymentDate(ZonedDateTime paymentDate) {
        this.paymentDate = paymentDate;
    }

    public List<PaymentItem> getPaymentItems() {
        return paymentItems;
    }

    public void setPaymentItems(List<PaymentItem> paymentItems) {
        this.paymentItems = paymentItems;
    }

    BigDecimal getTotalCost() {
        BigDecimal sum = BigDecimal.ZERO;
        for (PaymentItem next : paymentItems) {
            sum = sum.add(next.getFinalPrice());
        }
        return sum;
    }

    BigDecimal getTotalDiscount() {
        BigDecimal sum = BigDecimal.ZERO;
        for (PaymentItem paymentItem : paymentItems) {
            sum = sum.add(paymentItem.getRegularPrice().subtract(paymentItem.getFinalPrice()));
        }
        return sum;
    }
}
