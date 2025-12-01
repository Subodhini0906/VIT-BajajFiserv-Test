package com.bajaj.test.webhooktest;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;


@Service
public class WebhookService {

    private final RestTemplate rest = new RestTemplate();
    private final ObjectMapper mapper = new ObjectMapper();   

    private final String name = "Subodhini";
    private final String regNo = "22BCE10943";
    private final String email = "subodhini2022@vitbhopal.ac.in";

    public void startProcess() throws Exception {

        String url = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";

        ObjectNode body = mapper.createObjectNode();
        body.put("name", name);
        body.put("regNo", regNo);
        body.put("email", email);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<>(body.toString(), headers);

        ResponseEntity<String> response =
                rest.postForEntity(url, request, String.class);

        JsonNode json = mapper.readTree(response.getBody());

        String webhookUrl = json.get("webhook").asText();
        String accessToken = json.get("accessToken").asText();

        System.out.println("Webhook URL: " + webhookUrl);
        System.out.println("Access Token: " + accessToken);

        String finalSQL =
"SELECT d.DEPARTMENT_NAME, p.max_salary AS SALARY, CONCAT(e.FIRST_NAME, ' ', e.LAST_NAME) AS EMPLOYEE_NAME, TIMESTAMPDIFF(YEAR, e.DOB, CURDATE()) AS AGE FROM ( SELECT EMP_ID, DEPARTMENT, SUM(AMOUNT) AS max_salary FROM PAYMENTS JOIN EMPLOYEE ON PAYMENTS.EMP_ID = EMPLOYEE.EMP_ID WHERE DAY(PAYMENT_TIME) != 1 GROUP BY EMP_ID, DEPARTMENT ) p JOIN EMPLOYEE e ON e.EMP_ID = p.EMP_ID JOIN DEPARTMENT d ON d.DEPARTMENT_ID = p.DEPARTMENT JOIN ( SELECT DEPARTMENT, MAX(total_salary) AS highest_salary FROM ( SELECT EMP_ID, DEPARTMENT, SUM(AMOUNT) AS total_salary FROM PAYMENTS JOIN EMPLOYEE ON PAYMENTS.EMP_ID = EMPLOYEE.EMP_ID WHERE DAY(PAYMENT_TIME) != 1 GROUP BY EMP_ID, DEPARTMENT ) t GROUP BY DEPARTMENT ) h ON h.DEPARTMENT = p.DEPARTMENT AND h.highest_salary = p.max_salary";

        ObjectNode submitBody = mapper.createObjectNode();
        submitBody.put("finalQuery", finalSQL);

        HttpHeaders submitHeaders = new HttpHeaders();
        submitHeaders.setContentType(MediaType.APPLICATION_JSON);
        submitHeaders.set("Authorization", accessToken);

        HttpEntity<String> submitRequest =
                new HttpEntity<>(submitBody.toString(), submitHeaders);

        ResponseEntity<String> submitResponse =
                rest.postForEntity(webhookUrl, submitRequest, String.class);

        System.out.println("Submission Response:");
        System.out.println(submitResponse.getBody());
    }
}
