package inc.evil.refactorswitch.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Product {
    private Long id;
    private String imei;
    private String name;
    private double price;
    private ProductStatus status;
    private Category category;
}
