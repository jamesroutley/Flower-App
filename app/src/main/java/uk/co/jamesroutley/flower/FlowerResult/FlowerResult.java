package uk.co.jamesroutley.flower.FlowerResult;
// TODO: refactor flowerResult 'title' to 'species/genus'

/**
 * Created by admin on 20/01/15.
 */
public class FlowerResult {
    private String title, thumbnailUrl;

    public FlowerResult() {
    }

    public FlowerResult(String name, String thumbnailUrl) {
        this.title = name;
        this.thumbnailUrl = thumbnailUrl;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String name) {
        this.title = name;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }

}