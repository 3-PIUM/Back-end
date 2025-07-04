package project.domain.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import project.domain.aisummary.AiSummary;
import project.domain.aisummary.repository.AiSummaryRepository;
import project.domain.containingredient.ContainIngredient;
import project.domain.containingredient.repository.containIngredientRepository;
import project.domain.graph.Graph;
import project.domain.graph.repository.GraphRepository;
import project.domain.ingredient.enums.Risk;
import project.domain.item.Item;
import project.domain.item.dto.converter.ItemConverter;
import project.domain.item.dto.ItemResponse.*;
import project.domain.item.repository.ItemRepository;
import project.domain.itemimage.ItemImage;
import project.domain.itemimage.enums.ImageType;
import project.domain.itemimage.repository.ItemImageRepository;
import project.domain.member.Member;
import project.domain.wishlist.WishList;
import project.domain.wishlist.repository.WishlistRepository;
import project.global.kafka.dto.view.ViewEventDTO;
import project.global.kafka.service.view.ViewLogProducer;
import project.global.response.ApiResponse;
import project.global.response.exception.GeneralException;
import project.global.response.status.ErrorStatus;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemService {


    private final ItemImageRepository itemImageRepository;
    private final ItemRepository itemRepository;
    private final containIngredientRepository containIngredientRepository;
    private final AiSummaryRepository aiSummaryRepository;
    private final GraphRepository graphRepository;
    private final WishlistRepository wishlistRepository;
    private final ViewLogProducer viewLogProducer;
    private final ExchangeRateService exchangeRateService;

    private double changeToRate(String lang) {
        if ("KR".equalsIgnoreCase(lang)) {
            return 1.0;
        } else {
            return exchangeRateService.getRate(lang.toUpperCase());
        }
    }

    // 상품 정보 조회
    public ApiResponse<ItemInfoDTO> getItemInfo(Member member, Long itemId, String lang) {
        Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> new GeneralException(ErrorStatus.ITEM_NOT_FOUND));

        // 찜 상태
        boolean wishStatus = false;
        if (member != null) {
            Optional<WishList> isWishList = wishlistRepository.findByMemberIdAndItemId(
                member.getId(), itemId);
            wishStatus = isWishList.isPresent();
        }

        String mainImage = itemImageRepository.findByItemIdAndImageType(itemId, ImageType.MAIN)
            .stream()
            .map(ItemImage::getUrl)
            .findFirst()
            .orElseThrow(() -> new GeneralException(ErrorStatus.MAIN_IMAGE_NOT_FOUND));

        List<String> detailImages = itemImageRepository.findByItemIdAndImageType(itemId,
                ImageType.DETAIL).stream()
            .map(ItemImage::getUrl)
            .toList();

        // 조회 이벤트 발행(Kafka)
        ViewEventDTO viewEventDTO = ViewEventDTO.builder()
            .memberId(member != null ? member.getId() : 276523)
            .itemId(itemId)
            .subCategory(item.getSubCategory().getName())
            .eventTime(System.currentTimeMillis())
            .build();

        viewLogProducer.sendViewLog(viewEventDTO);
        double rate = changeToRate(lang);
        ItemInfoDTO itemInfoImages = ItemConverter.toItemInfoDTO(item, mainImage, detailImages,
            wishStatus, lang, rate);
        return ApiResponse.onSuccess(itemInfoImages);
    }


    // 성분 스코어(안전, 주의, 위험) 개수 조회
    public ApiResponse<RiskCountDTO> getRiskCount(Long itemId) {

        // 포함된 성분 저장
        List<ContainIngredient> containIngredients = containIngredientRepository.findByItemId(
            itemId);

        // 각 성분 스코어 개수 저장
        Map<Risk, Long> riskCounts = containIngredients.stream()
            .map(ci -> ci.getIngredient().getRisk())
            .collect(Collectors.groupingBy(
                Function.identity(),
                Collectors.counting()
            ));

        int dangerCount = riskCounts.getOrDefault(Risk.DANGER, 0L).intValue();
        int cautionCount = riskCounts.getOrDefault(Risk.CAUTION, 0L).intValue();
        int safeCount = riskCounts.getOrDefault(Risk.SAFE, 0L).intValue();
        int noneCount = riskCounts.getOrDefault(Risk.NONE, 0L).intValue();

        RiskCountDTO riskCountDTO = ItemConverter.toRiskCountDTO(itemId, safeCount, cautionCount,
            dangerCount, noneCount);
        return ApiResponse.onSuccess(riskCountDTO);
    }

    // 포함 성분 조회
    public ApiResponse<IngredientRankingDTO> getContainIngredients(Long itemId, String lang) {
        List<ContainIngredient> containIngredients = containIngredientRepository.findByItemId(
            itemId);

        IngredientRankingDTO ingredientScoreDTO = ItemConverter.toIngredientScoreDTO(itemId,
            containIngredients, lang);
        return ApiResponse.onSuccess(ingredientScoreDTO);
    }


    // 민감 주의 성분 조회
    public ApiResponse<CautionIngredientsDTO> getCautionIngredients(Long itemId, String lang) {
        List<ContainIngredient> containIngredients = containIngredientRepository.findByItemId(
            itemId);

        List<ContainIngredient> cautionIngredients = containIngredients.stream()
            .filter(ci -> ci.getIngredient().getRiskCategory(lang) != null)
            .toList();

        CautionIngredientsDTO cautionIngredientsDTO = ItemConverter.toCautionIngredientsDTO(itemId,
            cautionIngredients, lang);
        return ApiResponse.onSuccess(cautionIngredientsDTO);
    }


    // 피부 타입 별 AI 요약 조회
    public ApiResponse<AiSummaryListDTO> getAiSummary(Long itemId, String lang) {

        List<AiSummary> aiSummaries = aiSummaryRepository.findByItemIdOrderByRankingAsc(itemId);

        AiSummaryListDTO aiSummaryListDTO = ItemConverter.toAiSummaryListDTO(itemId, aiSummaries,
            lang);
        return ApiResponse.onSuccess(aiSummaryListDTO);
    }


    // 피부 타입 그래프 조회
    public ApiResponse<GraphListDTO> getGraphData(Long itemId, String lang) {
        List<Graph> graphs = graphRepository.findByItemId(itemId);

        GraphListDTO graphListDTO = ItemConverter.toGraphListDTO(itemId, graphs, lang);
        return ApiResponse.onSuccess(graphListDTO);
    }

    private List<Long> getWishListIds(Member member) {
        List<Long> wishListIds = new ArrayList<>();
        if (member != null) {
            List<Long> fetchedIds = wishlistRepository.findByMemberId(member.getId())
                .stream()
                .map(w -> w.getItem().getId())
                .toList();
            wishListIds.addAll(fetchedIds);
        }
        return wishListIds;
    }

}
