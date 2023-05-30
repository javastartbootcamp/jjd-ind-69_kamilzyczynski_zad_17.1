package pl.javastart.streamsexercise;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

class PaymentService {

    private final PaymentRepository paymentRepository;
    private final DateTimeProvider dateTimeProvider;

    PaymentService(PaymentRepository paymentRepository, DateTimeProvider dateTimeProvider) {
        this.paymentRepository = paymentRepository;
        this.dateTimeProvider = dateTimeProvider;
    }

    /*
    Znajdź i zwróć płatności posortowane po dacie rosnąco
     */
    List<Payment> findPaymentsSortedByDateAsc() {
        DateComparator dateComparator = new DateComparator();
        List<Payment> all = paymentRepository.findAll();
        List<Payment> result = all.stream()
                .sorted(dateComparator)
                .collect(Collectors.toList());
        return result;
    }

    /*
    Znajdź i zwróć płatności posortowane po dacie malejąco
     */
    List<Payment> findPaymentsSortedByDateDesc() {
        List<Payment> all = paymentRepository.findAll();
        DateComparator dateComparator = new DateComparator();
        Comparator<Payment> reversed = dateComparator.reversed();
        List<Payment> result = all.stream()
                .sorted(reversed)
                .collect(Collectors.toList());
        return result;
    }

    /*
    Znajdź i zwróć płatności posortowane po liczbie elementów rosnąco
     */
    List<Payment> findPaymentsSortedByItemCountAsc() {
        PaymentItemComparator paymentItemComparator = new PaymentItemComparator();
        List<Payment> all = paymentRepository.findAll();
        List<Payment> collect = all.stream()
                .sorted(paymentItemComparator)
                .collect(Collectors.toList());
        return collect;
    }

    /*
    Znajdź i zwróć płatności posortowane po liczbie elementów malejąco
     */
    List<Payment> findPaymentsSortedByItemCountDesc() {
        PaymentItemComparator paymentItemComparator = new PaymentItemComparator();
        Comparator<Payment> reversed = paymentItemComparator.reversed();
        List<Payment> all = paymentRepository.findAll();
        List<Payment> collect = all.stream()
                .sorted(reversed)
                .collect(Collectors.toList());
        return collect;
    }

    /*
    Znajdź i zwróć płatności dla wskazanego miesiąca
     */
    List<Payment> findPaymentsForGivenMonth(YearMonth yearMonth) {
        List<Payment> all = paymentRepository.findAll();
        List<Payment> result = all.stream()
                .filter(payment -> payment.getPaymentDate().getMonthValue() == yearMonth.getMonthValue())
                .collect(Collectors.toList());
        return result;

    }

    /*
    Znajdź i zwróć płatności dla aktualnego miesiąca
     */
    List<Payment> findPaymentsForCurrentMonth() {
        return findPaymentsForGivenMonth(YearMonth.now());
    }

    /*
    Znajdź i zwróć płatności dla ostatnich X dni
     */
    List<Payment> findPaymentsForGivenLastDays(int days) {
        ZonedDateTime now = dateTimeProvider.zonedDateTimeNow();
        ZonedDateTime date = now.minusDays(days);
        List<Payment> all = paymentRepository.findAll();
        List<Payment> result = all.stream()
                .filter(payment -> payment.getPaymentDate().isAfter(date) && payment.getPaymentDate().isBefore(now))
                .collect(Collectors.toList());
        return result;
    }

    /*
    Znajdź i zwróć płatności z jednym elementem
     */
    Set<Payment> findPaymentsWithOnePaymentItem() {
        List<Payment> all = paymentRepository.findAll();
        Set<Payment> result = all.stream()
                .filter(payment -> payment.getPaymentItems().size() == 1)
                .collect(Collectors.toSet());
        return result;
    }

    /*
    Znajdź i zwróć nazwy produktów sprzedanych w aktualnym miesiącu
     */
    Set<String> findProductsSoldInCurrentMonth() {
        List<Payment> paymentsForCurrentMonth = findPaymentsForCurrentMonth();
        Set<String> result = paymentsForCurrentMonth.stream()
                .map(Payment::getPaymentItems)
                .flatMap(List::stream)
                .map(PaymentItem::getName)
                .collect(Collectors.toSet());
        return result;
    }

    /*
    Policz i zwróć sumę sprzedaży dla wskazanego miesiąca
     */
    BigDecimal sumTotalForGivenMonth(YearMonth yearMonth) {
        List<Payment> paymentsForGivenMonth = findPaymentsForGivenMonth(yearMonth);
        BigDecimal result = paymentsForGivenMonth.stream()
                .map(Payment::getPaymentItems)
                .flatMap(List::stream)
                .map(PaymentItem::getFinalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return result;
    }

    /*
    Policz i zwróć sumę przyznanych rabatów dla wskazanego miesiąca
     */
    BigDecimal sumDiscountForGivenMonth(YearMonth yearMonth) {
        List<Payment> paymentsForGivenMonth = findPaymentsForGivenMonth(yearMonth);
        BigDecimal result = paymentsForGivenMonth.stream()
                .map(Payment::getPaymentItems)
                .flatMap(List::stream)
                .map(paymentItem -> paymentItem.getFinalPrice().subtract(paymentItem.getRegularPrice()))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return result;
    }

    /*
    Znajdź i zwróć płatności dla użytkownika z podanym mailem
     */
    List<PaymentItem> getPaymentsForUserWithEmail(String userEmail) {
        List<Payment> all = paymentRepository.findAll();
        List<PaymentItem> result = all.stream()
                .filter(payment -> payment.getUser().getEmail().equals(userEmail))
                .map(Payment::getPaymentItems)
                .flatMap(List::stream)
                .collect(Collectors.toList());
        return result;
    }

    /*
    Znajdź i zwróć płatności, których wartość przekracza wskazaną granicę
     */
    Set<Payment> findPaymentsWithValueOver(int value) {
        List<Payment> all = paymentRepository.findAll();
        Set<Payment> result = all.stream()
                .flatMap(payment -> payment.getPaymentItems().stream()
                        .filter(paymentItem -> paymentItem.getFinalPrice().intValueExact() > value)
                        .map(paymentItem -> payment))
                .collect(Collectors.toSet());
        return result;

    }

}
