import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class ApiTests {
    // Базовый URL для API
    private static final String BASE_URL = "https://qa-internship.avito.com/api/1";
    // Идентификатор продавца, для теста
    private static final int SELLER_ID = 123456;
    private String itemId; // Идентификатор созданного элемента

    @Before
    public void setup() {
        RestAssured.baseURI = BASE_URL;
        // Создаем элемент перед каждым тестом
        testCreateItem();
    }

    @Test
    public void testCreateItem() {
        // Тело запроса для создания элемента
        String requestBody = "{\n" +
                "    \"name\": \"Ручка\",\n" +
                "    \"price\": 777,\n" +
                "    \"sellerId\": " + SELLER_ID + ",\n" +
                "    \"statistics\": {\n" +
                "        \"contacts\": 444,\n" +
                "        \"likes\": 0,\n" +
                "        \"viewCount\": 444\n" +
                "    }\n" +
                "}";

        // Отправляем запрос на создание элемента
        Response response = given()
                .header("Content-Type", "application/json")
                .body(requestBody)
                .post("/item");

        // Проверяем, что статус код ответа равен 200 (успешно)
        response.then().statusCode(200);

        itemId = response.jsonPath().getString("id");
        System.out.println("Created Item ID: " + itemId);  // Выводим ID элемента для проверки
    }

    @Test
    public void testGetItemsBySeller() {
        if (itemId == null) {
            // Пропускаем тест, если itemId не установлен
            System.out.println("Item ID is null. Skipping testGetItemsBySeller.");
            return;
        }

        // Отправляем запрос на получение элементов по идентификатору продавца
        Response response = given()
                .pathParam("sellerID", SELLER_ID)
                .when()
                .get("/{sellerID}/item");

        System.out.println("Response Body: " + response.getBody().asString());  // Выводим тело ответа для проверки

        // Проверяем, что статус код ответа равен 200 (успешно)
        // Проверка свойств элемента по id
        response.then()
                .statusCode(200)
                .body("find { it.id == '" + itemId + "' }.name", equalTo("Ручка"))
                .body("find { it.id == '" + itemId + "' }.price", equalTo(777))
                .body("find { it.id == '" + itemId + "' }.sellerId", equalTo(SELLER_ID))
                .body("find { it.id == '" + itemId + "' }.statistics.contacts", equalTo(444))
                .body("find { it.id == '" + itemId + "' }.statistics.likes", equalTo(0))
                .body("find { it.id == '" + itemId + "' }.statistics.viewCount", equalTo(444));
    }

    @Test
    public void testGetItemById() {
        if (itemId == null) {
            // Пропускаем тест, если itemId не установлен
            System.out.println("Item ID is null. Skipping testGetItemById.");
            return;
        }

        // Отправка запроса на элемент по его идентификатору
        Response response = given()
                .pathParam("id", itemId)
                .when()
                .get("/item/{id}");

        System.out.println("Response Body: " + response.getBody().asString());  // Выводим тело ответа

        // Проверяем, что статус код ответа равен 200 (успешно)
        // Проверка свойств элемента по id
        response.then()
                .statusCode(200)
                .body("id", equalTo(itemId))
                .body("name", equalTo("Ручка"))
                .body("price", equalTo(777))
                .body("sellerId", equalTo(SELLER_ID))
                .body("statistics.contacts", equalTo(444))
                .body("statistics.likes", equalTo(0))
                .body("statistics.viewCount", equalTo(444));
    }
}
