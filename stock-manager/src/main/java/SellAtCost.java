import java.time.LocalDate;

// 120+ days: get my money back so I can buy stock that sells
public class SellAtCost implements PricingRule {

    @Override
    public boolean appliesTo(Item item, LocalDate today) {
        return item.getDaysInStock(today) >= 120;
    }

    @Override
    public String getSuggestion(Item item) {
        return String.format("Sell at cost (£%.2f) to free up the money", item.getCost());
    }
}
