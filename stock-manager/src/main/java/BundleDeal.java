import java.time.LocalDate;

// 90+ days: sell it together with another item
public class BundleDeal implements PricingRule {

    @Override
    public boolean appliesTo(Item item, LocalDate today) {
        return item.getDaysInStock(today) >= 90;
    }

    @Override
    public String getSuggestion(Item item) {
        return "Bundle it with a similar " + item.getBrand() + " item";
    }
}
