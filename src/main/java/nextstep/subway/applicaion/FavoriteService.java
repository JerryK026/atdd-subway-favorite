package nextstep.subway.applicaion;

import java.util.List;
import nextstep.member.domain.Member;
import nextstep.member.domain.MemberRepository;
import nextstep.subway.applicaion.dto.FavoriteRequest;
import nextstep.subway.applicaion.dto.FavoriteResponse;
import nextstep.subway.domain.Favorite;
import nextstep.subway.domain.FavoriteRepository;
import nextstep.subway.domain.Station;
import nextstep.subway.domain.StationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
@Service
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final StationRepository stationRepository;
    private final MemberRepository memberRepository;

    public FavoriteService(
        FavoriteRepository favoriteRepository,
        StationRepository stationRepository,
        MemberRepository memberRepository
    ) {
        this.favoriteRepository = favoriteRepository;
        this.stationRepository = stationRepository;
        this.memberRepository = memberRepository;
    }

    @Transactional
    public Long createFavorite(String email, FavoriteRequest favoriteRequest) {
        Member member = findMemberByEmail(email);
        Station source = findStationById(favoriteRequest.getSource());
        Station target = findStationById(favoriteRequest.getTarget());

        Favorite favorite = new Favorite(member.getId(), source, target);
        return favoriteRepository.save(favorite).getId();
    }

    public List<FavoriteResponse> findAll(String email) {
        Member member = findMemberByEmail(email);
        List<Favorite> favorites = favoriteRepository.findAllByMemberId(member.getId());

        return FavoriteResponse.listOf(favorites);
    }

    @Transactional
    public void delete(String email, Long id) {
        Member member = findMemberByEmail(email);
        Favorite favorite = findFavoriteById(id);

        if (!favorite.isSameMember(member)) {
            throw new IllegalArgumentException();
        }

        favoriteRepository.deleteById(id);
    }

    private Station findStationById(Long id) {
        return stationRepository.findById(id)
            .orElseThrow(IllegalArgumentException::new);
    }

    private Member findMemberByEmail(String email) {
        return memberRepository.findByEmail(email)
            .orElseThrow(IllegalArgumentException::new);
    }

    private Favorite findFavoriteById(Long id) {
        return favoriteRepository.findById(id)
            .orElseThrow(IllegalArgumentException::new);
    }
}
