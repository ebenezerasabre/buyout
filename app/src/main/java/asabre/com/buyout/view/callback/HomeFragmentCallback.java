package asabre.com.buyout.view.callback;

import asabre.com.buyout.service.model.Product;

public interface HomeFragmentCallback {
    void onCategoryClick(String category);
    void onCategorySeeAllClick();
    void onFeaturedProductsSeeAllClick();
    void onHotSalesSeeAllClick();
    void onTrendingSeeAllClick();
    void onHistorySeeAllClick();
    void onExploreClick();

}
