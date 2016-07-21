/*******************************************************************************
 *
 *  Copyright FUJITSU LIMITED 2016
 *
 *  Creation Date: 2016-05-23
 *
 *******************************************************************************/
package org.oscm.marketplace.dao;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.persistence.Query;

import org.oscm.converter.ParameterizedTypes;
import org.oscm.dataservice.local.DataService;
import org.oscm.domobjects.MarketplaceAccess;
import org.oscm.domobjects.Organization;

/**
 * Created by BadziakP on 2016-05-23.
 */
@Stateless
@LocalBean
public class MarketplaceAccessDao {

    @EJB(beanInterface = DataService.class)
    DataService dataService;

    public List<MarketplaceAccess> getForMarketplaceKey(long marketplaceKey) {
        Query query = dataService
                .createNamedQuery("MarketplaceAccess.findByMarketplace");
        query.setParameter("marketplace_tkey", marketplaceKey);
        return ParameterizedTypes.list(query.getResultList(),
                MarketplaceAccess.class);
    }

    public void removeAccessForMarketplace(long marketplaceKey) {
        Query query = dataService
                .createNamedQuery("MarketplaceAccess.removeAllForMarketplace");
        query.setParameter("marketplace_tkey", marketplaceKey);
        query.executeUpdate();
    }

    public List<Object[]> getOrganizationsWithMplAndSubscriptions(
            long marketplaceKey) {

        String querySelect = "SELECT o.tkey as orgKey, "
                + "o.organizationid as orgId, "
                + "o.name as name, "
                + "(SELECT true FROM marketplaceaccess ma where ma.organization_tkey=o.tkey and ma.marketplace_tkey=:marketplaceKey) as hasAccess, "
                + "(SELECT count(s.tkey) FROM subscription s WHERE s.organizationkey=o.tkey AND s.marketplace_tkey=:marketplaceKey AND s.status<>'DEACTIVATED') as subscriptions "
                + "FROM organization o";

        Query query = dataService.createNativeQuery(querySelect);

        query.setParameter("marketplaceKey", Long.valueOf(marketplaceKey));

        return ParameterizedTypes.list(query.getResultList(), Object[].class);
    }

    public List<Organization> getAllOrganizationsWithAccessToMarketplace(
            long marketplaceKey) {

        String queryString = "SELECT o.* FROM organization o INNER JOIN marketplaceaccess ma ON o.tkey = ma.organization_tkey WHERE ma.marketplace_tkey = :marketplaceKey";

        Query query = dataService.createNativeQuery(queryString,
                Organization.class);
        query.setParameter("marketplaceKey", new Long(marketplaceKey));

        return query.getResultList();
    }

}
