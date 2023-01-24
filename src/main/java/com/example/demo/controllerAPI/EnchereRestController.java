package com.example.demo.controllerAPI;


import com.example.demo.connex.Connexion;
import com.example.demo.dao.*;
import com.example.demo.models.Enchere;
import com.example.demo.models.Response;
import com.example.demo.models.TokenUser;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.List;

@RestController
@RequestMapping("/api/enchere")
@CrossOrigin
public class EnchereRestController {

    EnchereDao ed = new EnchereDao();

    ProduitDao p = new ProduitDao();
    HistoriqueOffreDao hod = new HistoriqueOffreDao();

    PrelevementEnchereDao ped = new PrelevementEnchereDao();
  Connexion con1 = new Connexion();
    Connection con;
    {
        try {
            con = objectBdd.ManipDb.pgConnect("postgres","railway","aQ7Q5V3Qb3RW0wTvtrTJ");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @GetMapping("listeEnchere")
    public ResponseEntity<List<Enchere>> getListeEnchere(){
        try{
            List<Enchere> list = ed.getListEnchere(con);
            for(Enchere e : list)
            {
                   ed.EnchereTerminer(con1,e.getIdenchere());
            }
            return new ResponseEntity<List<Enchere>>(list,HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("ficheEnchere")
    public ResponseEntity<List<Enchere>> getFicheEnchere(@RequestParam("idenchere") int idEnchere){
        try{
            return new ResponseEntity<List<Enchere>>(new EnchereDao().getFicheEnchere(con,idEnchere), HttpStatus.OK);
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @GetMapping("ListeEnchereUser")
    public ResponseEntity<List<Enchere>> ListeEnchereUser(@RequestHeader("token") String token){
        TokenUserDao tud = new TokenUserDao();
        TokenUser tu = new TokenUser();
        try{
            if(tud.validTokenUser(token)!=0)
            {
                tu = tud.getTokenUser(token);
                return new ResponseEntity<List<Enchere>>(new EnchereDao().getListeEnchereUser(con,tu.getIdUtilisateur()), HttpStatus.OK);
            }
            else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }

        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @PostMapping("AjoutEnchere")
    public Response AjoutEnchere(@RequestHeader("token") String token, @RequestParam("description") String description, @RequestParam("prixminimumvente") float prixminimumvente, @RequestParam("durreenchere") int durreenchere) throws Exception {
        Response response = new Response();
        TokenUserDao tud = new TokenUserDao();
        TokenUser tu;
        if(tud.validTokenUser(token)!=0)
        {
                tu = tud.getTokenUser(token);
                float montant_user = new UtilisateurDao().getCompteUser(tu.getIdUtilisateur(),con1);
                if(montant_user<prixminimumvente)
                {
                     response.setMessage("votre solde est insuffisante");
                }
                else
                {
                    int result = ed.AjouterEncher(con1,tu.getIdUtilisateur(),description,prixminimumvente,durreenchere);
                    //compte user
                    hod.setCompteUser(tu.getIdUtilisateur(),prixminimumvente,con1);
                    //commission
                    ped.Inserer(con1,result,ed.MontantPrelevee(result));
                    response.setMessage("votre vente a été bien prise en compte");
                }
        }
       else
       {
            response.setMessage("veuillez dabord vous authentifier");
       }
        return response;
    }


    @PostMapping("ProduitEnchere")
    public Response ProduitEnchere(@RequestParam("idEnchere") int idEnchere,@RequestParam("idProduit") int idProduit,@RequestHeader("token") String token) throws Exception {
        Response response = new Response();
        TokenUserDao tud = new TokenUserDao();
        if(tud.validTokenUser(token)!=0)
        {
            p.AjouterProduitEnchere(con1,idEnchere,idProduit);
        }
        else{
            response.setMessage("token expiré");
        }
        response.setMessage("mety");
        return response;
    }


    @PostMapping("AjouterPhoto")
    public Response AjoutPhotoEnchere(@RequestParam("idproduit") int idproduit,@RequestParam("photo") String photo,@RequestHeader("token") String token) throws Exception {
        Response response = new Response();
        TokenUserDao tud = new TokenUserDao();

           if(tud.validTokenUser(token)!=0)
           {
            p.AjouterPhotoProduit(con1,idproduit,photo);
           }
           else{
              response.setMessage("token expiré");
         }
        response.setMessage("mety");
        return response;
    }

    @PostMapping("/rechercheAvancée")
    public List<Enchere> advancedSearch(@RequestParam(required = false, value="datedebut") String startDate,
                                        @RequestParam(required = false, value="datefin") String endDate,
                                        @RequestParam(required = false, value="description") String category,
                                        @RequestParam(required = false, value="status") String auctionStatus,
                                        @RequestParam(required = false, value="motcle") String keywords){

        PreparedStatement stmt = ed.generateStatement(con1,startDate,endDate,category,auctionStatus,keywords);
        List<Enchere> encheres= null;
        try {
            encheres = ed.getListEnchereRecherche(stmt);
        } catch (Exception e) {
        }
        return encheres;
    }
}
