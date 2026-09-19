package chemlab.services;

import chemlab.domain.exceptions.FailedToLoadPTException;
import chemlab.domain.repository.ElementRepository;
import chemlab.infrastructure.pubchem.PubChemElement;
import chemlab.service.chemistry.DefaultElementService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static java.util.Arrays.asList;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ElementServiceUnitTests {
    @Mock
    private ElementRepository elmRepo;

    @InjectMocks
    private DefaultElementService elmService;


    @Test
    @DisplayName("should list all elements")
    void testListAll() throws FailedToLoadPTException {
        // Arrange
        PubChemElement elm1 = new PubChemElement();
        elm1.setAtomicNumber("1");
        elm1.setAtomicMass("4.5");
        PubChemElement elm2 = new PubChemElement();
        elm2.setAtomicNumber("2");
        elm2.setAtomicMass("10.3");
        // define what will happen
        when(elmRepo.findAll()).thenReturn(asList(elm1, elm2));
        // Act
        List<PubChemElement> allPubChemElements = elmService.getAllElements();
        // Assert
        assertThat(allPubChemElements).containsExactly(elm1, elm2);
        verify(elmRepo).findAll();
    }

    @Test
    @DisplayName("should find Element with symbol of H")
    void testFindBySymbol() {
        // Arrange
        PubChemElement testElm = new PubChemElement();
        testElm.setSymbol("H");
        when(elmRepo.findElementBySymbol("H")).thenReturn(testElm);
        // Act
        PubChemElement actualElm = elmService.getElementBySymbol("H");
        // Assert
        assertEquals(testElm.getSymbol(), actualElm.getSymbol());
        verify(elmRepo).findElementBySymbol("H");
    }
}
