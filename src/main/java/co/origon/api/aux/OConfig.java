package co.origon.api.aux;

import com.googlecode.objectify.annotation.Cache;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;


@Entity
@Cache(expirationSeconds = 60)
public class OConfig
{
    public @Id String category;
    public String configJson;
}
