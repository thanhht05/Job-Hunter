package vn.JobHunter.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import vn.JobHunter.domain.Skill;
import vn.JobHunter.domain.Subscriber;
import vn.JobHunter.repository.SkillRepository;
import vn.JobHunter.repository.SubscriberReposirory;
import vn.JobHunter.util.exception.IdInvalidException;

@Service
public class SubscriberService {
    private final SubscriberReposirory subscriberReposirory;
    private final SkillRepository skillRepository;

    public SubscriberService(SubscriberReposirory subscriberReposirory, SkillRepository skillRepository) {
        this.subscriberReposirory = subscriberReposirory;
        this.skillRepository = skillRepository;
    }

    public Subscriber creatSubscriber(Subscriber subscriber) throws IdInvalidException {
        if (this.subscriberReposirory.existsByEmail(subscriber.getEmail())) {
            throw new IdInvalidException("Subscriber da ton tai");
        }

        if (subscriber.getSkills() != null) {
            List<Long> reqSkills = subscriber.getSkills().stream().map(item -> item.getId())
                    .collect(Collectors.toList());
            List<Skill> skills = this.skillRepository.findByIdIn(reqSkills);
            subscriber.setSkills(skills);
        }
        return this.subscriberReposirory.save(subscriber);
    }

    public Subscriber updateSubscriber(Subscriber subscriber) throws IdInvalidException {
        Optional<Subscriber> subsriberOptional = this.subscriberReposirory.findById(subscriber.getId());
        if (subsriberOptional.isEmpty()) {
            throw new IdInvalidException("Subsciber khong ton tai");
        }

        Subscriber curSubscriber = subsriberOptional.get();
        if (subscriber.getSkills() != null) {
            List<Long> reqSkills = subscriber.getSkills().stream().map(i -> i.getId()).collect(Collectors.toList());
            List<Skill> dbSkills = this.skillRepository.findByIdIn(reqSkills);

            curSubscriber.setSkills(dbSkills);
        }
        return this.subscriberReposirory.save(curSubscriber);
    }

    public void deleteSubscriber(Long id) throws IdInvalidException {
        Optional<Subscriber> subsOptional = this.subscriberReposirory.findById(id);
        if (subsOptional.isEmpty()) {
            throw new IdInvalidException("Subsciber khong ton tai");
        }
        Subscriber curSubscriber = subsOptional.get();
        curSubscriber.getSkills().forEach(skill -> skill.getSubscribers().remove(curSubscriber));

        this.subscriberReposirory.delete(curSubscriber);

    }
}
