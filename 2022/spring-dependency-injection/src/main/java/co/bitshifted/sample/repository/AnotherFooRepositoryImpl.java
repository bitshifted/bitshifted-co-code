package co.bitshifted.sample.repository;

import org.springframework.stereotype.Repository;

@Repository("repositoryTwo")
public class AnotherFooRepositoryImpl implements FooRepository {
    @Override
    public void save() {
        System.out.println("Calling AnotherFooRepositoryImpl.save()");
    }
}
