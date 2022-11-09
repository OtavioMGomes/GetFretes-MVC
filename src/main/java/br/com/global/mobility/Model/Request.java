package br.com.global.mobility.Model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.fasterxml.jackson.annotation.JsonIgnore;

import br.com.global.mobility.Utilities.FreightPercentage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name="TblPEDIDO")
public class Request {

    @Id
    @Column(name="ID_PEDIDO")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    
    @Column(name="VL_PERC_FRETE")
    private Float percentage = FreightPercentage.getPercentage();

    @Column(name="VL_FRETE")
    private Float freightValue;

    @Column(name="DT_INCLUSAO")
    private LocalDateTime inclusionDate;

    @Transient
    private Status status;

    @ManyToOne
    @JoinColumn(name="ID_USUARIO_CLIENTE")
    private User userClient;

    @ManyToOne
    @JoinColumn(name="ID_USUARIO_PRESTADOR")
    private User userTransporter;

    @ManyToOne
    @JoinColumn(name="ID_ENDERECO")
    private Address address;

    @ManyToOne
    @JoinColumn(name="ID_MULTIPLICADOR")
    private FreightFactor factor;

    @OneToMany(fetch = FetchType.LAZY, mappedBy="request", cascade = CascadeType.ALL)
    private List<RequestItem> requestItemList = new ArrayList<RequestItem>();

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY, mappedBy="request", cascade = CascadeType.ALL)
    private List<RequestStatusRel> statusList = new ArrayList<RequestStatusRel>();

    public void addUser(User user){
        user.addToList(this);
    }

    public void addAddress(Address address){
        address.addToList(this);
    }

    public void addFactor(FreightFactor factor){
        factor.addToList(this);
    }

    public void addToList(RequestItem requestItem){
        requestItem.setRequest(this);
        this.getRequestItemList().add(requestItem);
    }

    public void addToList(RequestStatusRel requestStatusRel){
        requestStatusRel.setRequest(this);
        this.getStatusList().add(requestStatusRel);
    }
    
    public void setInclusionDate() {
        if (this.inclusionDate == null)
            this.inclusionDate = LocalDateTime.now();
    }

    public void freightCalculation(){

        Float value = 0f;

        for(RequestItem ri : this.getRequestItemList()){

            if(ri.getValid() == 1){
                value += ri.getValue();
            }

        }

        value = value * this.factor.getValue() * this.percentage;

        this.freightValue = value;

    }

    public String setCurrentStatus(){

        RequestStatusRel rel = null;

        for(RequestStatusRel rs : statusList){

            if(rel == null){
                rel = rs;
            }else{
                if(rel.getInclusionDate().isBefore(rs.getInclusionDate())){
                    rel = rs;
                }
            }

        }

        if(rel != null && rel.getStatus() != null){
            this.status = rel.getStatus();
            return this.status.getStatus().toString();
        }

        return null;
    }
    
}