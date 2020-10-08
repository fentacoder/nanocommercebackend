package com.infotechnano.nanocommerce.rest;

import com.infotechnano.nanocommerce.models.Bid;
import com.infotechnano.nanocommerce.services.BidService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/bids")
@CrossOrigin(origins = {"http://localhost:4200"})
public class BidController {

    private final BidService bidService;

    @Autowired
    public BidController(BidService bidService){
        this.bidService = bidService;
    }

    @PostMapping("add")
    public Integer addBid(@RequestBody Bid bid){
        return bidService.addBid(bid);
    }

    @PostMapping(path = "paginate")
    public List<Bid> paginate(@RequestBody HashMap<String,String> tempDict){
        try{
            return bidService.paginate(Integer.parseInt(tempDict.get("currentPage")),
                    Boolean.parseBoolean(tempDict.get("earlier")),Boolean.parseBoolean(tempDict.get("lastPage")),
                    Integer.parseInt(tempDict.get("skipped")),Integer.parseInt(tempDict.get("idxBound")));
        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
}
