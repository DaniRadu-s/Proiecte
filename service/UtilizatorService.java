package com.example.guiex1.services;



import com.example.guiex1.domain.*;
import com.example.guiex1.repository.Repository;
import com.example.guiex1.repository.dbrepo.FriendshipDbRepository;
import com.example.guiex1.repository.dbrepo.MessageDbRepository;
import com.example.guiex1.repository.dbrepo.ProfileDbRepository;
import com.example.guiex1.repository.dbrepo.UtilizatorDbRepository;
import com.example.guiex1.utils.events.*;
import com.example.guiex1.utils.observer.Observable;
import com.example.guiex1.utils.observer.Observer;
import com.example.guiex1.utils.paging.Page;
import com.example.guiex1.utils.paging.Pageable;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class UtilizatorService implements Observable<Event> {
    private UtilizatorDbRepository repo;
    private FriendshipDbRepository repofriend;
    private MessageDbRepository repoMessage;
    private ProfileDbRepository profileRepo;
    private List<Observer<Event>> observers=new ArrayList<>();

    public UtilizatorService(UtilizatorDbRepository repo, FriendshipDbRepository repofriend, MessageDbRepository repoMessage, ProfileDbRepository repoProfile) {
        this.repo = repo;
        this.repofriend = repofriend;
        this.repoMessage= repoMessage;
        this.profileRepo = repoProfile;
    }


    public Utilizator addUtilizator(Utilizator user, String s) {
        if(repo.save(user).isEmpty()){
            UtilizatorEntityChangeEvent event = new UtilizatorEntityChangeEvent(ChangeEventType.ADD, user);
            notifyObservers(event);
            return null;
        }
        Utilizator u = searchName(user.getFirstName(),user.getLastName());
        UtilizatorEntityChangeEvent event = new UtilizatorEntityChangeEvent(ChangeEventType.ADD, user);
        notifyObservers(event);
        repo.setParola(u,s);
        return user;
    }

    public String getHashedPasswordFromDB(Long userID) {
        String sql = "SELECT hashedPassword FROM passwords WHERE userID = ?";
        String hashedPassword = null;

        // Conectează-te la baza de date
        try (Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/Map_lab", "postgres", "Dani");
             PreparedStatement ps = connection.prepareStatement(sql)) {

            // Setează parametrii pentru interogare
            ps.setLong(1, userID);

            // Execută interogarea
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    hashedPassword = rs.getString("hashedPassword");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return hashedPassword; // Returnează parola hash-uită (sau null dacă nu există)
    }

    public Utilizator deleteUtilizator(Long id){
        Optional<Utilizator> user=repo.delete(id);
        Optional<Profile> pr = profileRepo.delete(id);
        if (user.isPresent()) {
            notifyObservers(new UtilizatorEntityChangeEvent(ChangeEventType.DELETE, user.get()));
            return user.get();
        }
        return null;
    }

    public Iterable<Utilizator> getAll(){
        return repo.findAll();
    }

    public Iterable<Prietenie> getAllFriendship(){
        return repofriend.findAll();
    }

    @Override
    public void addObserver(Observer<Event> e) {
        observers.add(e);

    }


    @Override
    public void removeObserver(Observer<Event> e) {
        //observers.remove(e);
    }

    @Override
    public void notifyObservers(Event t) {
        observers.stream().forEach(x->x.update(t));
    }


    public Utilizator updatePasswordUtilizator(Utilizator user, String s) {
        Optional<Utilizator> oldUser = repo.findOne(user.getId());
        if(oldUser.isPresent()){
            Optional<Utilizator> newUser = repo.updatePassword(user,s);
            if(newUser.isEmpty())
                notifyObservers(new UtilizatorEntityChangeEvent(ChangeEventType.UPDATE, user, oldUser.get()));
            return newUser.orElse(null);
        }
        return oldUser.orElse(null);
    }
    public Utilizator updateUtilizator(Utilizator u) {
        Optional<Utilizator> oldUser=repo.findOne(u.getId());
        if(oldUser.isPresent()) {
            Optional<Utilizator> newUser=repo.update(u);
            if (newUser.isEmpty())
                notifyObservers(new UtilizatorEntityChangeEvent(ChangeEventType.UPDATE, u, oldUser.get()));
            return newUser.orElse(null);
        }
        return oldUser.orElse(null);
    }

    public Prietenie addFriendship(Prietenie p) {
        String s = "1";
        Prietenie friendship = repofriend.findOne(p.getId()).orElse(null);
        if(friendship != null && friendship.getStatus().equals("pending")){
            repofriend.update(new Prietenie(friendship.getId().getLeft(),friendship.getId().getRight(),friendship.getDate(),"active"));
            UtilizatorEntityChangeEvent event = new UtilizatorEntityChangeEvent(ChangeEventType.UPDATE, null);
            notifyObservers(event);
            return null;
        }
        if(repofriend.save(p).isEmpty()){
            FriendshipEntityChangeEvent event = new FriendshipEntityChangeEvent(ChangeEventType.FRIEND, p);
            notifyObservers(event);
            return null;
        }
        return p;

    }

    public Prietenie deleteFriendship(Tuple<Long,Long> id) {
        Optional<Prietenie> friendship=repofriend.delete(id);
        if (friendship.isPresent()) {
            notifyObservers(new UtilizatorEntityChangeEvent(ChangeEventType.DELETE, null));
            return friendship.get();
        }
        return null;
    }

    public Utilizator searchUser(Long id){
        return repo.findOne(id).orElse(null);
    }

    public Utilizator searchName(String firstName, String lastName){
        List<Utilizator> list = StreamSupport.stream(repo.findAll().spliterator(),false)
                .filter(u->u.getFirstName().equals(firstName)&&u.getLastName().equals(lastName))
                .collect(Collectors.toList());
        if(list.isEmpty()){
            return null;
        }
        else return list.get(0);
    }

    public Prietenie findFriendship(Tuple<Long,Long>id){
        return repofriend.findOne(id).orElse(null);
    }

    public Message getChat(Long id1, Long id2){
        return repoMessage.findChat(id1,id2);
    }

    public Message addMessage(Message m){
        if(repoMessage.save(m).isEmpty()){
            MessageEntityChangeEvent event = new MessageEntityChangeEvent(ChangeEventType.ADD, m);
            notifyObservers(event);
            return null;
        }
        return m;
    }

    public int getFriend(Utilizator utilizator) {
        Iterable<Tuple<Utilizator,String>> messages = utilizator.getFriends();
        int s = 0;
        for(Tuple<Utilizator,String> m : messages){
            if(m.getRight().equals("active"))
                s++;
        }
        return s;
    }

    public List<Message> findAll(Long id, Long id1) {
        return repoMessage.findAllMes(id,id1);
    }

    public Page<Utilizator> findAllOnPageS(Long id, Pageable pageable) {
        Page<Prietenie> page = repofriend.findAllOnPage(id,pageable);
        return new Page<Utilizator>(StreamSupport.stream(page.getElementsOnPage().spliterator(),false)
                .map(f->f.getId().getLeft().equals(id) ? repo.findOne(f.getId().getRight()).get() : repo.findOne(f.getId().getLeft()).get())
                .collect(Collectors.toList()),page.getTotalNumberOfElements());
    }

    public Profile addProfile(Profile profile) {
        if(profileRepo.save(profile).isEmpty()){
            return null;
        }
        return profile;
    }
    public Profile updateProfile(Profile p) {
        Optional<Profile> oldp=profileRepo.findOne(p.getUid());
        if(oldp.isPresent()) {
            Optional<Profile> newp=profileRepo.update(p);
            if (newp.isEmpty()) {
                notifyObservers(new ProfileEntityChangeEvent(ChangeEventType.PROFILE, p, oldp.get()));

            }
            return newp.orElse(null);
        }
        return oldp.orElse(null);
    }

    public Profile deleteProfile(Long id){
        Optional<Profile> user=profileRepo.delete(id);
        if (user.isPresent()) {
            notifyObservers(new ProfileEntityChangeEvent(ChangeEventType.DELETE, user.get()));
            return user.get();
        }
        return null;
    }

    public Profile getProfile(Long id){
        return profileRepo.findProfile(id).orElse(null);
    }
}
