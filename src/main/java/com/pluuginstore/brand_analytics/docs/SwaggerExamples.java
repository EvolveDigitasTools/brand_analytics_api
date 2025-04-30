package com.pluuginstore.brand_analytics.docs;

public class SwaggerExamples {
    public static final String SUCCESS_INVENTORY_RESPONSE = """
        {
            "success": true,
            "message": "All inventory retrieved successfully",
            "data": {
                "inventory": [
                    {
                        "SKU Code": "SKU001",
                        "Category": "Electronics",
                        "Product Title": "Wireless Mouse",
                        "SAP Code": "SAP123",
                        "EAN": "1234567890123",
                        "salesLast15Days": 75,
                        "Current Inventory": [
                            { "count": 50, "expiry": null },
                            { "count": 10, "expiry": "2024-12-31" }
                        ]
                    },
                    {
                        "SKU Code": "SKU002",
                        "Category": "Accessories",
                        "Product Title": "USB-C Cable",
                        "SAP Code": "SAP456",
                        "EAN": "9876543210987",
                        "salesLast15Days": 150,
                        "Current Inventory": [
                            { "count": 200, "expiry": null }
                        ]
                    }
                ]
            }
        }
    """;

    public static final String ERROR_INVENTORY_RESPONSE = """
        {
            "success": false,
            "message": "An internal error occurred while retrieving inventory: [Error Details]",
            "data": {
                "source": "InventoryController -> getAllInventory"
            }
        }
    """;
}

