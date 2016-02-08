package uk.co.todddavies.literarylog.models;

import com.google.common.base.Optional;

/**
 * Models a single reading item such as an article.
 */
public final class Reading {

  public final String name;
  public final String description;
  public final int id;
  public final Status status;
  public final Type type;
  public final Optional<Integer> rating;
  public final Optional<String> link;
  
  private Reading(String name, String description, Status status, Type type,
      Optional<Integer> rating, Optional<String> link) {
    this.name = name;
    this.description = description;
    this.status = status;
    this.rating = rating;
    this.link = link;
    this.type = type;
    // Compute the hash
    int hash = 19;
    hash = 37 * hash + name.hashCode();
    hash = 37 * hash + description.hashCode();
    hash = 37 * hash + (rating.isPresent() ? rating.get() : 0);
    hash = 37 * hash + (link.isPresent() ? link.get().hashCode() : 0);
    hash = 37 * hash + status.code;
    id = hash;
  }
  
  @Override
  public int hashCode() {
    return id;
  }
  
  public static Builder newBuilder() {
    return new Builder();
  }
  
  public static Builder newBuilder(Reading existing) {
    Builder out = new Builder(
        existing.name,existing.description, existing.status, existing.type);
    if (existing.link.isPresent()) {
      out.setLink(existing.link.get());
    }
    if (existing.rating.isPresent()) {
      out.setRating(existing.rating.get());
    }
    return out;
  }
  
  public static Builder newBuilder(String name, String description, Status status,
      Type type) {
    return new Builder(name, description, status, type);
  }
  
  // TODO: Improve the interface in-line with Effective Java
  public static final class Builder {
    private String name;
    private String description;
    private Status status;
    private Type type;
    private Optional<Integer> rating = Optional.absent();
    private Optional<String> link = Optional.absent();
    
    private Builder() {}
    
    private Builder(String name, String description, Status status, Type type) {
      this.name = name;
      this.description = description;
      this.status = status;
      this.type = type;
    }
    
    public Reading build() {
      return new Reading(name, description, status, type, rating, link);
    }
    
    public boolean isComplete() {
      return name != null && description != null && status != null && type != null;
    };

    public String getName() {
      return name;
    }

    public Builder setName(String name) {
      this.name = name;
      return this;
    }

    public Optional<Integer> getRating() {
      return rating;
    }

    public Builder setRating(int rating) {
      this.rating = Optional.of(rating);
      return this;
    }

    public String getDescription() {
      return description;
    }

    public Builder setDescription(String description) {
      this.description = description;
      return this;
    }

    public Status getStatus() {
      return status;
    }

    public Builder setStatus(Status status) {
      this.status = status;
      return this;
    }

    public Optional<String> getLink() {
      return link;
    }

    public Builder setLink(String link) {
      this.link = Optional.of(link);
      return this;
    }

    public Type getType() {
      return type;
    }

    public Builder setType(Type type) {
      this.type = type;
      return this;
    }
  }  
}