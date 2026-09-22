package chemlab.services.integration;

import chemlab.domain.exceptions.FailedToLoadPTException;
import chemlab.domain.service.chemistry.ElementService;
import chemlab.domain.service.user.UserService;
import chemlab.infrastructure.fastapiworker.service.FastApiWorkerService;
import chemlab.infrastructure.persistence.user.UserPersistenceAdapter;
import chemlab.infrastructure.persistence.user.UserReactionPersistenceAdapter;
import chemlab.infrastructure.pubchem.PubChemElement;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@SpringBootTest
class ElementServiceIntegrationTests {

    @Autowired
    ElementService elmService;
    @MockitoBean
    FastApiWorkerService fastApiWorkerService;
    @MockitoBean
    UserPersistenceAdapter userPersistenceAdapter;
    @MockitoBean
    UserReactionPersistenceAdapter userReactionPersistenceAdapter;
    @MockitoBean
    UserService userService;

    @Test
    @DisplayName("should list all 118 elements from database")
    void testListAll() throws FailedToLoadPTException {
        List<PubChemElement> allPubChemElements = elmService.getAllElements();
        assertEquals(118, allPubChemElements.size());
    }

	@Test
	@DisplayName("should equal H (hydrogen)")
	void testFindBySymbol() {
		PubChemElement elm = elmService.getElementBySymbol("H");
		assertEquals("H", elm.getSymbol());
	}

    @Test
    @DisplayName("should fail to find element by symbol D")
    void testFailToFindBySymbol() {
        PubChemElement elm = elmService.getElementBySymbol("D");
        assertNull(elm);
    }
}
