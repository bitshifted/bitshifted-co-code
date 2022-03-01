package co.bitshifted.sample.service;

import co.bitshifted.sample.repository.FooRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

@Service
public class RepoService {

    private final FooRepository repoOne;
    private final FooRepository repoTwo;

    public RepoService(@Qualifier("repositoryOne") FooRepository repoOne,
                       @Qualifier("repositoryTwo") FooRepository repoTwo) {
        this.repoOne = repoOne;
        this.repoTwo = repoTwo;
    }

    public void methodOne() {
        repoOne.save();
    }

    public void methodTwo() {
        repoTwo.save();
    }
}
