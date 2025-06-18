package chemlab.services;

import chemlab.domain.chemistry.ElementService;
import chemlab.model.chemistry.Element;
import chemlab.domain.repository.chemistry.ElementRepository;
import chemlab.exceptions.domain.FailedToLoadPTException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@SpringBootTest
class ElementServiceTest {

    @Autowired
    private ElementService elmService;

    @MockitoBean
    private ElementRepository elmRepo;

    private Element elm;

    @BeforeEach
    public void setupMock() {
        elm = mock(Element.class);
    }

    @Test
    public void testMockCreation() {
        assertNotNull(elmService);
        assertNotNull(elm);
    }

    @Test
    @DisplayName("should list all elements")
    void testListAll() throws FailedToLoadPTException {
        // Arrange
        Element elm1 = new Element();
        elm1.setAtomicNumber("1");
        elm1.setAtomicMass("4.5");
        Element elm2 = new Element();
        elm2.setAtomicNumber("2");
        elm2.setAtomicMass("10.3");
        // define what will happen
        when(elmRepo.findAll()).thenReturn(asList(elm1, elm2));
        // Act
        List<Element> allElements = elmService.getAllElements();
        // Assert
        assertThat(allElements).containsExactly(elm1, elm2);
        verify(elmRepo).findAll();
    }

    @Test
    @DisplayName("should find Element with symbol of H")
    void testFindBySymbol() {
        // Arrange
        Element testElm = new Element();
        testElm.setSymbol("H");
        when(elmRepo.findElementBySymbol("H")).thenReturn(testElm);
        // Act
        Element actualElm = elmService.getElementBySymbol("H");
        // Assert
        assertEquals(testElm.getSymbol(), actualElm.getSymbol());
        verify(elmRepo).findElementBySymbol("H");
    }

//    @Test
//    @DisplayName("should find Element with atomic number of 1")
//    void testFindByAtomicNumber() {
//        Element testElm = new Element();
//        testElm.setAtomicNumber("1");
//        when(elmService.getElementByAtomicNumber("1")).thenReturn(testElm);
//        Element actualElm = elmService.getElementByAtomicNumber("1");
//        assertEquals(testElm.getAtomicNumber(), actualElm.getAtomicNumber());
//        verify(elmService).getElementByAtomicNumber("1");
//    }

}
