package com.planner.backend;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.springframework.test.util.AssertionErrors.assertEquals;
import static org.springframework.test.util.AssertionErrors.assertNotNull;

@SpringBootTest
@ActiveProfiles("test")
public class AccountControllerUnitTests {
    @Autowired
    AccountController accountController;

    @AfterEach
    void Cleanup()
    {
        accountController.clearDatabase();
    }

    @Test
    void Create_Account_Succeeds()
    {
        Account result = accountController.newAccount(new Account("test1@test.com", "mypassword"));
        assertNotNull("The output was null!", result);
        assertNotNull("The output today value was null!", result.getTodayTemplate());
        assertEquals("There were not zero elements in the templates!", 0, result.getTemplates().size());
    }
}
