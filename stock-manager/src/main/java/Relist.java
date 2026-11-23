import java.time.LocalDate;

// 60+ days: give the listing a refresh
public class Relist implements PricingRule {

    @Override
    public boolean appliesTo(Item item, LocalDate today) {
        return item.getDaysInStock(today) >= 60;
    }

    @Override
    public String getSuggestion(Item item) {
        return "Retake photos and relist";
    }
}
