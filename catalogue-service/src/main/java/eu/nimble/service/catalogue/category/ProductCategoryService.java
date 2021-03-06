package eu.nimble.service.catalogue.category;

import eu.nimble.service.catalogue.model.category.Category;
import eu.nimble.service.catalogue.model.category.CategoryTreeResponse;

import java.util.List;

/**
 * This service deals with identification of the product category to be used for associating the products / services
 * to be published onto NIMBLE.
 * Created by suat on 03-Mar-17.
 */
public interface ProductCategoryService {
    public Category getCategory(String categoryId);

    public List<Category> getProductCategories(String categoryName);

    public List<Category> getProductCategories(String categoryName, boolean forLogistics);

    public String getTaxonomyId();

    public CategoryTreeResponse getCategoryTree(String categoryId);

    public List<Category> getParentCategories(String categoryId);

    public List<Category> getChildrenCategories(String categoryId);

    public List<Category> getRootCategories();

    public List<Category> getAllCategories();
}