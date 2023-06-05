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
        return paymentRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Payment::getPaymentDate))
                .collect(Collectors.toList());
    }

    /*
    Znajdź i zwróć płatności posortowane po dacie malejąco
     */
    List<Payment> findPaymentsSortedByDateDesc() {
        return paymentRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(Payment::getPaymentDate).reversed())
                .collect(Collectors.toList());
    }

    /*
    Znajdź i zwróć płatności posortowane po liczbie elementów rosnąco
     */
    List<Payment> findPaymentsSortedByItemCountAsc() {
        return paymentRepository.findAll()
                .stream()
                .sorted(Comparator.comparing((Payment payment) -> payment.getPaymentItems().size()))
                .collect(Collectors.toList());
    }

    /*
    Znajdź i zwróć płatności posortowane po liczbie elementów malejąco
     */
    List<Payment> findPaymentsSortedByItemCountDesc() {
        return paymentRepository.findAll()
                .stream()
                .sorted(Comparator.comparing((Payment payment) -> payment.getPaymentItems().size()).reversed())
                .collect(Collectors.toList());
    }

    /*
    Znajdź i zwróć płatności dla wskazanego miesiąca
     */
    List<Payment> findPaymentsForGivenMonth(YearMonth yearMonth) {
        return paymentRepository.findAll()
                .stream()
                .filter(payment -> payment.getPaymentDate().getMonthValue() == yearMonth.getMonthValue() &&
                        yearMonth.getYear() == payment.getPaymentDate().getYear())
                .collect(Collectors.toList());
    }

    /*
    Znajdź i zwróć płatności dla aktualnego miesiąca
     */
    List<Payment> findPaymentsForCurrentMonth() {
        return findPaymentsForGivenMonth(dateTimeProvider.yearMonthNow());
    }

    /*
    Znajdź i zwróć płatności dla ostatnich X dni
     */
    List<Payment> findPaymentsForGivenLastDays(int days) {
        ZonedDateTime now = dateTimeProvider.zonedDateTimeNow();
        ZonedDateTime date = now.minusDays(days);
        return paymentRepository.findAll()
                .stream()
                .filter(payment -> payment.getPaymentDate().isAfter(date) && payment.getPaymentDate().isBefore(now))
                .collect(Collectors.toList());
    }

    /*
    Znajdź i zwróć płatności z jednym elementem
     */
    Set<Payment> findPaymentsWithOnePaymentItem() {
        return paymentRepository.findAll()
                .stream()
                .filter(payment -> payment.getPaymentItems().size() == 1)
                .collect(Collectors.toSet());
    }

    /*
    Znajdź i zwróć nazwy produktów sprzedanych w aktualnym miesiącu
     */
    Set<String> findProductsSoldInCurrentMonth() {
        List<Payment> paymentsForCurrentMonth = findPaymentsForCurrentMonth();
        return paymentsForCurrentMonth.stream()
                .map(Payment::getPaymentItems)
                .flatMap(List::stream)
                .map(PaymentItem::getName)
                .collect(Collectors.toSet());
    }

    /*
    Policz i zwróć sumę sprzedaży dla wskazanego miesiąca
     */
    BigDecimal sumTotalForGivenMonth(YearMonth yearMonth) {
        List<Payment> paymentsForGivenMonth = findPaymentsForGivenMonth(yearMonth);
        return paymentsForGivenMonth.stream()
                .map(Payment::getPaymentItems)
                .flatMap(List::stream)
                .map(PaymentItem::getFinalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /*
    Policz i zwróć sumę przyznanych rabatów dla wskazanego miesiąca
     */
    BigDecimal sumDiscountForGivenMonth(YearMonth yearMonth) {
        List<Payment> paymentsForGivenMonth = findPaymentsForGivenMonth(yearMonth);
        return paymentsForGivenMonth.stream()
                .map(Payment::getTotalDiscount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
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
                .filter(payment -> payment.getTotalCost().compareTo(BigDecimal.valueOf(value)) > 0)
                .collect(Collectors.toSet());
        return result;

    }
}
