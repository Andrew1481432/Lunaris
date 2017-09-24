package org.lunaris.entity;

import org.lunaris.Lunaris;
import org.lunaris.entity.data.Attribute;
import org.lunaris.event.EventManager;
import org.lunaris.event.entity.EntityDamageByEntityEvent;
import org.lunaris.event.entity.EntityDamageEvent;

/**
 * Created by RINES on 14.09.17.
 */
public class LivingEntity extends Entity {

    protected LivingEntity(long entityID) {
        super(entityID);
    }

    public float getHealth() {
        return getAttribute(Attribute.MAX_HEALTH).getValue();
    }

    public float getMaxHealth() {
        return getAttribute(Attribute.MAX_HEALTH).getMaxValue();
    }

    public void setHealth(float value) {
        setAttribute(Attribute.MAX_HEALTH, value);
    }

    public void setMaxHealth(float value) {
        Attribute a = getAttribute(Attribute.MAX_HEALTH);
        a.setMaxValue(value);
        setHealth(a.getValue());
    }

    public void damage(double damage) {
        damage(EntityDamageEvent.DamageCause.UNKNOWN, damage);
    }

    public void damage(EntityDamageEvent.DamageCause cause, double damage) {
        EntityDamageEvent event = new EntityDamageEvent(this, cause, damage);
        Lunaris.getInstance().getEventManager().call(event);
        if(event.isCancelled())
            return;
        damage0(damage);
    }

    public void damage(Entity damager, double damage) {
        EntityDamageEvent event1 = new EntityDamageEvent(this, EntityDamageEvent.DamageCause.ENTITY_ATTACK, damage);
        EventManager manager = Lunaris.getInstance().getEventManager();
        manager.call(event1);
        if(event1.isCancelled())
            return;
        EntityDamageByEntityEvent event2 = new EntityDamageByEntityEvent(damager, this, event1.getDamage());
        manager.call(event2);
        if(event2.isCancelled())
            return;
        damage0(event2.getFinalDamage());
    }

    void damage0(double damage) {
        setHealth((float) (getHealth() - damage));
    }

    @Override
    public void tick() {
        super.tick();
        if(!(this instanceof Player) && getHealth() < .5D)
            remove();
    }

}
