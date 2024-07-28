package com.sip.ams;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

//import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import com.sip.ams.entities.Provider;
import com.sip.ams.repositories.ProviderRepository;
import com.sip.ams.services.ProviderService;

@ExtendWith(MockitoExtension.class)
public class ProviderServiceTest {

	@InjectMocks
	ProviderService service;

	// We use the @Mock annotation to inject a mock for an instance variable that we
	// can use anywhere in the test class
	@Mock
	ProviderRepository dao;

	@Test
	void testFindAllProviders() {
		// Given
		List<Provider> list = new ArrayList<Provider>();
		Provider providerOne = new Provider("Med Amine MEZGHICH", "amine.mezghich@ensi-uma.tn",
				"DevOps Tools Training");
		Provider providerTwo = new Provider("Heinz Kabutz", "heinz@javaspecialists.eu", "OCP-17 Training");
		Provider providerThree = new Provider("Yong Mook Kim", "ymyun@knu.ac.kr",
				"Full-stack Spring-Boot Angular Training");

		list.add(providerOne);
		list.add(providerTwo);
		list.add(providerThree);

		when(dao.findAll()).thenReturn(list);

		// test
		List<Provider> providerList = service.findAll();
		// Then
		assertEquals(3, providerList.size());
		verify(dao, times(1)).findAll();
	}

	@Test
	void testCreateOrSaveProvider() {
		// Given
		Provider provider = new Provider("Med Amine MEZGHICH", "amine.mezghich@ensi-uma.tn", "DevOps Tools Training");
		// When
		service.create(provider);
		// Then
		verify(dao, times(1)).save(provider);
	}
}
