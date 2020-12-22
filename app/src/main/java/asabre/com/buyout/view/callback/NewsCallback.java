package asabre.com.buyout.view.callback;

import asabre.com.buyout.view.ui.AdapterNews;

public interface NewsCallback {
    void newsThumbsUp(String newsId, AdapterNews.ViewHolder holder);
    void newsThumbsDown(String newsId, AdapterNews.ViewHolder holder);
}
