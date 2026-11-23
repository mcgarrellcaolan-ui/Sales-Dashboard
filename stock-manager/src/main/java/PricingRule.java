import java.time.LocalDate;

// A suggestion for what to do with slow stock.
// Each class that implements this gives its own suggestion (polymorphism),
// so the report can treat them all as a PricingRule.
public interface PricingRule {

    boolean appliesTo(Item item, LocalDate today);

    String getSuggestion(Item item);
}
