package com.infotechnano.nanocommerce.dao;

import com.infotechnano.nanocommerce.models.Bid;

import java.util.List;

public interface BidDao {
    Integer addBid(Bid bid);
    List<Bid> paginate(Integer currentPage, boolean earlier, boolean lastPage, Integer skipped, Integer idxBound);
}
