## strong acid/strong base

function strongacid (Ca,Cb,Va,Vb)
Kw = 1e-14;

## solve quadratic eqn for H: a*H^2 + b*H + c = 0;
a = Va + Vb;
b = Cb.*Vb - Ca.*Va;
c = -Kw.*(Va + Vb);
H1 = (-b + sqrt(b.^2 - 4.*a.*c))./(2.*a);
#H2 = (-b - sqrt(b.^2 - 4.*a.*c))./(2.*a);

## find pH at each Vb value
pH1 = -log10(H1);
#pH2 = -log10(H2);

## plot pH versus Vb
plot(Vb,pH1); axis ([0,75,0,14]); xlabel('Vb'); ylabel('pH');
#plot(Vb,pH2);

endfunction