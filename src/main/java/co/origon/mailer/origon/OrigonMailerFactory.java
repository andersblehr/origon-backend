package co.origon.mailer.origon;

import co.origon.mailer.api.Mailer;
import co.origon.mailer.api.MailerFactory;
import co.origon.api.model.ofy.DaoFactoryOfy;

public class OrigonMailerFactory implements MailerFactory {

    @Override
    public Mailer mailer(String languageCode) {
        return new OrigonMailer(languageCode, new DaoFactoryOfy());
    }
}
