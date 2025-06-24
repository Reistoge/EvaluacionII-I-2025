

import filter.ExtremeValueFilter;
import filter.UnitNormalizerFilter;
import filter.ValidatorFilter;
import model.RawData;
import model.CleanData;
import org.junit.Test;
import repository.CleanDataRepository;
import java.time.LocalDateTime;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class FilterTest {

    @Test
    public void testValidatorFilterValid() throws Exception {

        RawData data = new RawData("temperature", LocalDateTime.now(), 25, "C");

        ValidatorFilter filter = new ValidatorFilter();

        assertNotNull(filter.apply(data));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testValidatorFilterInvalidType() throws Exception {

        RawData data = new RawData("invalid", LocalDateTime.now(), 25, "C");

        new ValidatorFilter().apply(data);
    }

    @Test
    public void testUnitNormalizerFilterTemperature() throws Exception {


        RawData data = new RawData("temperature", LocalDateTime.now(), 100, "F");

        UnitNormalizerFilter filter = new UnitNormalizerFilter();

        RawData result = filter.apply(data);

        assertTrue(result.getMeasuredValue() < 70);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testExtremeValueFilterTemperatureOutOfRange() throws Exception {

        RawData data = new RawData("temperature", LocalDateTime.now(), 1000, "C");

        new ExtremeValueFilter().apply(data);
    }

    @Test
    public void testRepositoryWithMockito() {

        CleanDataRepository repo = mock(CleanDataRepository.class);

        CleanData data = new CleanData("mp", LocalDateTime.now(), 50);

        repo.save(data);

        verify(repo).save(data);

    }
}