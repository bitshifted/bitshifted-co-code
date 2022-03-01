package co.bitshifted.sample.repository;

import org.springframework.stereotype.Repository;

@Repository("repositoryOne")
public class FooRepositoryImpl implements FooRepository{
    @Override
    public void save() {
        System.out.println("Calling FooRepositoryImpl.save()");
    }
}
