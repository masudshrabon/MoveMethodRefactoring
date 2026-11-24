# SampleShop – test input for MoveMethodRefactoring

A small shop system (7 types in `src/com/sample/shop`) with **3 planted methods that belong in another class** (Move Method candidates).
It is only input data for the tool; it is not part of its build.

| Class | Role |
|---|---|
| `Customer` | name, email, phone, address fields + getters, `getFullAddress()` |
| `OrderItem` | product name, unit price, quantity, `getSubtotal()` |
| `Order` | items, customer, discount, `calculateTotal()`, `formatShippingLabel()` |
| `Invoice` | order, tax rate, `computeTax()`, `computeAmountDue()`, `printCustomerContact()` |
| `Inventory` | warehouse stock levels, `isInStock()`, `needsReorder()`, `restock()` |
| `ReportGenerator` | report title/author, `buildReportHeader()`, `summarizeStock()` |
| `Shippable` | interface – must never be a target |

## Ground truth (by design)

| Method | Should move to |
|---|---|
| `Order.formatShippingLabel()` | `Customer` |
| `Invoice.printCustomerContact()` | `Customer` |
| `ReportGenerator.summarizeStock(Inventory, String[])` | `Inventory` |

All other methods are well placed.

## Expected output

`expected_results.csv` is the output predicted by an independent reference
implementation (Python, `javalang` parser + NLTK Porter stemmer) with default options.
It contains 5 recommendations:

- the 3 planted methods above (detected), and
- 2 false positives the algorithm produces by design:
  - `Customer.getName()` -> `OrderItem` (0.2081 vs 0.2391): a one-term method (`name`);
    cosine favours the shorter class document that also contains `name`.
  - `Invoice.computeTax()` -> `Order` (0.2874 vs 0.3558): it calls `order.calculateTotal()`,
    so `order`, `calcul`, `total` match `Order` more than `tax`, `rate` match `Invoice`.

## How to run (Eclipse)

Run `moveMethodRefactoring.Main` with Program arguments:

```
dataset/SampleShop dataset/SampleShop/actual_results.csv
```

Then compare `actual_results.csv` with `expected_results.csv`
(same rows and scores = the Java parser extracts the same terms as the reference).
