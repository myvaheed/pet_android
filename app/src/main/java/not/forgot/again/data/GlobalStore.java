package not.forgot.again.data;

import not.forgot.again.model.repositories.NFARepository;

public class GlobalStore {
    NFARepository nfaRepository;

    public GlobalStore(NFARepository nfaRepository) {
        this.nfaRepository = nfaRepository;
    }

    public NFARepository getNfaRepository() {
        return nfaRepository;
    }
}
