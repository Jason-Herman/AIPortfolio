package edu.gatech.health.objects.query;

import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Created by Trevoris on 4/10/2016.
 * Test Implementation
 */
public interface TestCoordinateRepository extends CrudRepository<TestCoordinate, String> {

    List<TestCoordinate> findByPatientId(String patientId);
}
