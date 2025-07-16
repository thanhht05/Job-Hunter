package vn.JobHunter.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import vn.JobHunter.domain.Subscriber;
import vn.JobHunter.service.SubscriberService;
import vn.JobHunter.util.annotation.ApiMessage;
import vn.JobHunter.util.exception.IdInvalidException;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/api/v1")
public class SubscriberController {
    private SubscriberService subscriberService;

    private SubscriberController(SubscriberService subscriberService) {
        this.subscriberService = subscriberService;
    }

    @PostMapping("/subscribers")
    @ApiMessage("create subscriber")
    public ResponseEntity<Subscriber> handleCreate(@RequestBody Subscriber subscriber) throws IdInvalidException {

        return ResponseEntity.status(HttpStatus.CREATED).body(this.subscriberService.creatSubscriber(subscriber));
    }

    @PutMapping("/subscribers")
    @ApiMessage("Update subscriber")
    public ResponseEntity<Subscriber> update(@RequestBody Subscriber subscriber) throws IdInvalidException {

        return ResponseEntity.ok().body(this.subscriberService.updateSubscriber(subscriber));
    }

    @DeleteMapping("/subscribers/{id}")
    @ApiMessage("Delete subscriber")
    public ResponseEntity<Void> delete(@PathVariable("id") Long id) throws IdInvalidException {
        this.subscriberService.deleteSubscriber(id);
        return ResponseEntity.ok().body(null);
    }

}
